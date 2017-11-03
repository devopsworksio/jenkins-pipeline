def call(String message='No message!', String key) {
    timeout(time: 5, unit: 'SECONDS') {
        if (key != 'None' || key != null) {
            try {
                jiraComment body: message, issueKey: key
            } catch (err) {
                echo "\033[1;31m[Error] JIRA Notification failed! ${err.message}   \033[0m"
            }
        }
    }
}