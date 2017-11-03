def call() {
    stage('QA Approval') {
        try {
            userInput = input(
                    id: 'Proceed1', message: 'Approved by QA Engineer ?', parameters: [
                    [$class: 'BooleanParameterDefinition', defaultValue: false, description: 'QA Approval', name: 'Please approve this Pull Request']
            ]
            )
        } catch (err) {
            def user = err.getCauses()[0].getUser()
        }
    }
    if (userInput == true) {
        println "\033[1;31m[Error] User input: true   \033[0m"
        updateGitHubCheck 'QA Approval', 'QA Approval', 'SUCCESS'
    } else {
        updateGitHubCheck 'QA Approval', 'QA Approval', 'FAILURE'
    }
}