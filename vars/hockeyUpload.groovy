def call(String apkName, String appId, String notes=null) {
    notes = "Built from: ${env.CHANGE_BRANCH}, developed by ${CHANGE_AUTHOR}"
    withCredentials([string(credentialsId: 'HOCKEY_JENKINS_API_TOKEN', variable: 'HOCKEY_API_TOKEN')]) {
        echo " \033[1;32m Hockeyapp uploading ${apkName}   \033[0m"
        try {
            step([$class: 'HockeyappRecorder', applications: [[apiToken: env.HOCKEY_API_TOKEN, downloadAllowed: true, filePath: apkName, mandatory: false, notifyTeam: false, releaseNotesMethod: [$class: 'ManualReleaseNotes', releaseNotes: notes], uploadMethod: [$class: 'VersionCreation', appId: appId]]], debugMode: true, failGracefully: false])
        } catch (err) {
            println "Error! \033[1;31m[Error] Failed to upload ${apkName} - ${err}   \033[0m"

        }
    }
}