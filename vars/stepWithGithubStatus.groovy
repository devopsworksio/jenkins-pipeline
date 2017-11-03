def call(String context, Closure buildStep, Closure postBuildStep) {

    updateGitHubCheck context, "Running $context...", "PENDING"

    def change = env.CHANGE_TITLE ?: env.BRANCH_NAME
    def url = env.CHANGE_URL ?: "https://github.com/${gitHubAccount}/${gitHubRepo}/tree/${env.BRANCH_NAME}"

    try {
        timeout(10) {
            buildStep.call()
        }
        currentBuild.result = 'SUCCESS'
        updateGitHubCheck context, "$context passed!", "SUCCESS"
        slackFeed "Step: ${context} ${currentBuild.result} for <${url} | ${change}> "

    } catch (err) {
        updateGitHubCheck context, "$context failed!", "FAILURE"
        notifyJira "${env.CHANGE_AUTHOR}!  Build Failed! See ${env.BUILD_URL} for details.", "${env.JIRA_ISSUE}"
        slackFeed "Step: ${context} ${currentBuild.result} for <${url} | ${change}> "
        currentBuild.result = 'FAILURE'
        ansiColor('xterm') {
            println "\033[1;31m[Error] Step ${context} failed! Reason: ${err}   \033[0m"
        }
        error('Stopping pipeline!')
    } finally {
        postBuildStep.call()
    }

    println "\033[1;32m[Info] ${context} - ${currentBuild.result}\033[0m"
}

