def call(boolean runMainTestsOnSecondaryVariants) {
    stage('Unit Tests') {
        node(env.NODE_LABEL) {
            prepareWorkspace()
            stepWithGithubStatus('Unit Tests', {
                sh "./gradlew testUkReleaseUnitTestCoverage -PrunMainTestsForSecondaryVariants=${runMainTestsOnSecondaryVariants.toString()} ${env.GRADLE_PARAMS}"
            }, {
                stash(name: 'junit-report', includes: '**/TEST*.xml', alllowEmpy: true)
                stash(name: 'jacoco-exec', includes: '**/jacoco/*.exec', alllowEmpy: true)
            })
            stash(name: 'classes', includes: '**/*.class', allowEmpty: false)
            step([$class: 'WsCleanup', notFailBuild: true])
        }
    }
}