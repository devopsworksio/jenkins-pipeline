def call() {

    stage('Publish reports') {
        node(env.NODE_LABEL) {
            step([$class: 'WsCleanup', notFailBuild: true])
            try {
                unstash 'junit-report'
                step([$class: 'JUnitResultArchiver', testResults: '**/TEST*.xml'])

            } catch (err) {
                println 'Junit report publishing failed for: ' + err.message
            }

            try {
                unstash 'jacoco-exec'
                step([$class: 'JacocoPublisher', execPattern: 'app/build/jacoco/testUkDebugUnitTest.exec'])
            } catch (err) {
                println 'Jacoco report publishing failed for: ' + err.message

            } finally {
                step([$class: 'WsCleanup', notFailBuild: true])
            }
        }
    }

}