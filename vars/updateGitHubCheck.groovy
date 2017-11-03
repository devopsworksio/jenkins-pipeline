def call(String context, String description, String status) {
    timeout(time: 10, unit: 'SECONDS') {
        try {
            githubNotify account: env.gitHibAccount, context: "${context}", credentialsId: env.gitHubCredsId ,
                    description: "${description}",
                    gitApiUrl: '',
                    repo: env.gitHubRepo,
                    sha: env.GIT_COMMIT,
                    status: "${status}",
                    targetUrl: ''
        } catch (err) {
            echo "\033[1;31m[Error] Github reporting failed ... : ${err.message}   \033[0m"
        }
    }
}
