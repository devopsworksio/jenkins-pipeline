import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import hudson.model.*
import com.cloudbees.groovy.cps.NonCPS


class common implements Serializable {

    def steps
    def env

    common(steps, env) {
        this.steps = steps
        this.env = env
    }


    @NonCPS
    def getGitHubSHA(String changeId) {
        try {
            // TODO: parameterize
            withCredentials([[$class: 'StringBinding', credentialsId: 'github', variable: 'GITHUB_TOKEN']]) {

                def apiUrl = "https://api.github.com/repos/${env.gitHubRepo}/${env.gitHubRepo}/pulls/${changeId}"
                def response = sh(returnStdout: true, script: "curl -s -H \"Authorization: Token ${env.GITHUB_TOKEN}\" -H \"Accept: application/json\" -H \"Content-type: application/json\" -X GET ${apiUrl}").trim()
                def jsonSlurper = new JsonSlurper()
                def data = jsonSlurper.parseText("${response}")
                return data.head['sha']
            }
        } catch (error) {
            echo "${error}"
            echo "${response}"
            error("Failed to get GitHub SHA for PR")
        }
    }

    @NonCPS
    def reportFinalBuildStatus() {
        def body = """
        Build Succeeded!...
        Hockey Version: ${env.BUILD_COUNTER}
        Jenkins URL: ${env.BUILD_URL}
        Git Hub URL: ${env.CHANGE_URL}
        Git Commit: ${env.GIT_COMMIT}
        """
        echo "Job result : ${currentBuild.result}"
        if (currentBuild.result == null || currentBuild.result == 'SUCCESS') {
            updateGithubCheck 'Jenkins Job', 'Job successful!', 'SUCCESS'
            notifyJira body, env.JIRA_ISSUE

        } else {
            updateGithubCheck 'Jenkins Job', 'Job failed!', 'FAILURE'
            notifyJira "Build Failed!", env.JIRA_ISSUE
        }

    }

    @NonCPS
    def gradleParametersWithVersion() {
        if (env.BUILD_COUNTER == null) {
            error("\033[1;31m[Error] env.BUILD_COUNTER can not be null. Stopping pipeline. Please consult the logs for the root cause.   \033[0m")

        }
        params = gradleParameters()
        return "-PcustomVersionCode=${env.BUILD_COUNTER}  " + params
    }

    def buildCounter() {
        steps.build 'android-build-counter'
        def job = Jenkins.instance.getItemByFullName('android-build-counter')
        def item = job.getLastSuccessfulBuild().number
        return item

    }

}
