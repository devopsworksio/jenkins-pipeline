def call(scmVars) {
    def issue
    def get_issue

    try {
        if (env.CHANGE_ID) {
            get_issue = '''
            echo ${CHANGE_BRANCH} | grep -Po '(?!\\/)([a-zA-Z]+-[0-9]+)(?!\\d)'
            '''
            println ' \033[1;32m[Info] this is a PR build !  \033[0m'

        } else {
            get_issue = '''
            echo ${BRANCH_NAME} | grep -Po '(?!\\/)([a-zA-Z]+-[0-9]+)(?!\\d)'
            '''
            println ' \033[1;21]m[Info] this is a BRANCH build !  \033[0m'

        }
    } catch (err) {
        error(err.message)
    }

    try {
        env.GIT_COMMIT = scmVars.GIT_COMMIT
        println "\033[1;32m[Info]  GIT_COMMIT: ${env.GIT_COMMIT}   \033[0m"
    } catch (err) {
        error(err.message)
    }

    try {
        issue = steps.sh(script: get_issue, returnStdout: true).trim()
        println "\033[1;32m[Info] Jira Issue Key: ${issue} \033[0m"
        env.JIRA_ISSUE = issue
    } catch (err) {
        println "\033[1;31m[Error] Error! cant't work out JIRA issue from branch name. This is not fatal but please adhere to naming convention!\\033[0m"
        println "${err.message}"
    }

}
