def call() {
    node(env.NODE_LABEL) {
        step([$class: 'WsCleanup', notFailBuild: true])
        unstash 'sources'
        stepWithGithubStatus('Checkstyle', {
            sh "${env.GROOVY} checkStyle ${env.GRADLE_PARAMS}"
        }, {
            step([$class: 'CheckStylePublisher', canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', failedTotalAll: '9999', healthy: '', pattern: '**/checkstyle/checkstyle.xml', unHealthy: '1'])
        })
    }
}
