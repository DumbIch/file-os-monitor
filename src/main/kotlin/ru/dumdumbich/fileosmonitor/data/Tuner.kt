package ru.dumdumbich.fileosmonitor.data

/**
 * <h3>file-os-monitor</h3>
 * @description
 * <p>Configuring application settings</p>
 * @author DumbIch
 * @date 2023-10-10 13:10
 **/

class Tuner(args: Array<String>) {

    private val userName: String = System.getProperty("user.name").lowercase()
    private val appName = "file-os-monitor"
    private val homeDirectory: String = System.getProperty("user.home")
    private val appDirectory: String = System.getProperty("user.dir")
    lateinit var trackingPathString: String
    lateinit var loggerPathString: String

    init {
        if (args.isNotEmpty()) {
            if (args.size >= 2) {
                if (args[0] == "--target") {
                    trackingPathString = args[1]
                } else if (args[0] == "--logger") {
                    loggerPathString = args[1]
                }
            }
            if (args.size == 4) {
                if (args[2] == "--logger") {
                    loggerPathString = args[3]
                }
            }
        } else {
            when (userName) {
                "master" -> {
                    trackingPathString = "$homeDirectory/ftp"
                    loggerPathString = "$homeDirectory/app/$appName/log"
                }

                "registrar", "client" -> {
                    trackingPathString = "$appDirectory/$appName/temp"
                    loggerPathString = "$appDirectory/$appName/log"
                }
            }
        }

        println("App directory : $appDirectory")
        println("Tracking path : $trackingPathString")
        println("Logger path : $loggerPathString")

    }

}
