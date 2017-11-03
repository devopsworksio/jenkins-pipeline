def call() {
    node(env.NODE_LABEL) {
        step([$class: 'WsCleanup', notFailBuild: true])
        unstash 'sources'
        stepWithGithubStatus('Lint', {
            sh "./gradlew lintSuite ${env.GRADLE_PARAMS}"
        }, {
            step([$class: 'LintPublisher', canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '0', pattern: '', unHealthy: '30', useStableBuildAsReference: true])
        })
    }
}