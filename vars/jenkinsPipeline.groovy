def call (String nodeLabel, String androidHome, String gitRef=null, String buildType=null) {

    env.NODE_LABEL = nodeLabel
    env.ANDROID_HOME = androidHome
    env.GROOVY = "/home/jenkins/tools/groovy/bin/gradle"

    properties([disableConcurrentBuilds(), [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '7', artifactNumToKeepStr: '20', daysToKeepStr: '20', numToKeepStr: '20']]]);
    def common = new common(steps, env)
    def conf = new buildConfig()

    ansiColor('xterm') {
        node(env.NODE_LABEL) {
            env.JAVA_HOME = tool name: 'Java8', type: 'jdk'
            env.JAVA7_HOME = env.JAVA_HOME
            env.GRADLE_PARAMS = conf.gradleArgs
            currentBuild.result = 'SUCCESS'

            updateGitHubCheck 'Jenkins Job', 'Running job...', 'PENDING'

            stage('Checkout') {
                step([$class: 'WsCleanup', notFailBuild: true])

                if (gitRef != null) {
                    env.BRANCH_NAME = buildType
                    git branch: gitRef, credentialsId: 'github-username-password', url: 'https://github.com/pockethub/PocketHub.git'

                } else {
                    def scmVars = checkout scm
                    gitUtils(scmVars)
                }
                def props = readProperties file: "${env.WORKSPACE}/pipeline.properties"

            // load into env which is global scope
            props.each { k, v ->
                env."${k}" = v
            }

                stash(name: 'sources' )
                step([$class: 'WsCleanup', notFailBuild: true])

            }
        }

        parallel(
                'Lint': {
                    stage('Lint') {
                        checkLint()
                    }
                },
                'Checkstyle': {
                    stage('Checkstyle') {
                        checkStyle()
                    }
                },
                'Unit Test': {
                    stage('Unit Tests') {
                        unitTests(false)
                    }
                }
        )

        milestone(label: 'Static Analysis')
        publishReports()
        env.BUILD_COUNTER = common.buildCounter()

        stage('Package') {
            switch (env.BRANCH_NAME) {
                case ~/^(PR.*|feature\/.*|pullRequest|master)/:
                    buildApks(conf.alphaReleases)
                    break
                default:
                    error("Branch name is not right for pushing APKs to hockey!")
            }

        }

        milestone(label: 'Packaging')
        stage('Finish') {
            reportFinalBuildStatus()
            slackFeed currentBuild.result
        }

    }

    milestone(label: 'QA Approval')

    stage('QA Approval') {
        try {
            userInput = input(
                    id: 'Proceed1', message: 'Approved by QA Engineer ?', parameters: [
                    [$class: 'BooleanParameterDefinition', defaultValue: false, description: 'QA Approval', name: 'Please approve this Pull Request']
            ])
        } catch (err) {
            def user = err.getCauses()[0].getUser()
        }

        if (userInput == true) {
            println ">>> User input: true <<<"
            updateGitHubCheck 'QA Approval', 'QA Approval', 'SUCCESS'
        } else {
            println ">>> User input: da <<<"
            updateGitHubCheck 'QA Approval', 'QA Approval', 'FAILURE'
        }
    }

}




