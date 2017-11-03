def call(String type = 'babylon') {
    step([$class: 'WsCleanup', notFailBuild: true])
    unstash 'sources'
    def unzip = '''
            rm -fr app/src/main/assets/dist
            mkdir -p app/src/main/assets/dist
            '''
    try {
        stdout = sh(returnStdout: true, script: unzip)
    } catch (err) {
        println err.message
        error('\033[1;31m[Error] Unzip failed!   \033[0m')
    }

}
