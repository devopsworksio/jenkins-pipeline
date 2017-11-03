// slack config stored centrally
def call(String msg, String color ='##F35A00') {
    try {
        slackSend channel: env.slackChannel, message: msg.trim(), color: color
    } catch (error) {
        // this is not fatal just annoying
        echo "\033[1;31m[Error] Slack feed updated failed!   \033[0m"
    }

}