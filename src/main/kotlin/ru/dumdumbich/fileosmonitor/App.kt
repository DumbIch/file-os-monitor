package ru.dumdumbich.fileosmonitor


import kotlinx.coroutines.*
import org.apache.commons.io.monitor.FileAlterationObserver
import ru.dumdumbich.fileosmonitor.data.FileListener
import java.io.File


suspend fun main() {

    val interval = 1000L
    val path = "/home/master/app/file-os-monitor/temp/"
    val listener = FileListener()
    val observer = FileAlterationObserver(File(path))
    observer.addListener(listener)

    val coroutineScope = CoroutineScope(Dispatchers.Default)
    coroutineScope.launch {
        while (coroutineScope.isActive) {
            observer.checkAndNotify()
            delay(interval)
        }
    }.join()

}


/*
fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}
*/