def call() {
    def body = """
    |Job result : ${currentBuild.currentResult}
    |Hockey Version: ${env.BUILD_COUNTER}
    |Jenkins URL: ${env.BUILD_URL}
    |Git Hub URL: ${env.CHANGE_URL}
    |Git Commit: ${env.GIT_COMMIT}
    """.stripMargin()

    if (currentBuild.currentResult =='SUCCESS') {
        updateGitHubCheck 'Jenkins Job', 'Job successful!', 'SUCCESS'
        notifyJira body, env.JIRA_ISSUE
        slackFeed body

    } else {
        updateGitHubCheck 'Jenkins Job', 'Job failed!', 'FAILURE'
        notifyJira "Build Failed! See  ${env.BUILD_URL}", env.JIRA_ISSUE
        slackFeed body
    }
}