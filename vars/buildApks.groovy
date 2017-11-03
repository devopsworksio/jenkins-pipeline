def call(Map releases, Map signingKeys=[:]) {
    def extraArgs = ""
    def builds = releases.collectEntries { it ->
        def gradleStuff = "./gradlew ${it.value.gradleTask} ${env.GRADLE_PARAMS} -PjenkinsFastDexguardBuildsEnabled ${extraArgs}"
        [(it.value.label): {
            node(env.NODE_LABEL) {
                step([$class: 'WsCleanup', notFailBuild: true])
                if (it.value.label =~ /bupa/) {
                    prepareWorkspace('bupa')
                } else {
                    prepareWorkspace()
                }
                unstash 'classes'
                stepWithGithubStatus(it.value.label, {
                    if (it.value.keys) {
                        withCredentials(signingKeys[it.value.keys]) {
                            sh gradleStuff
                        }
                    } else {
                        sh gradleStuff
                    }

                }, {
                    archiveArtifacts()
                    if (it.value.hockeyId) (
                            hockeyUpload('**/*.apk', "${it.value.hockeyId}")
                    )
                })
            }
        }

        ]
    }

    parallel builds

}

