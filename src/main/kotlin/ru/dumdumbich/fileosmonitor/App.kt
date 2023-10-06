package ru.dumdumbich.fileosmonitor


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.nio.file.*


suspend fun main() {
    val targetPath = "/home/master/app/file-os-monitor/temp/"
    val path = Paths.get(targetPath)

    val watchService: WatchService = withContext(Dispatchers.IO) {
        FileSystems.getDefault().newWatchService()
    }
    val watchKey = withContext(Dispatchers.IO) {
        path.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE
        )
    }

    do {
        delay(1000L)
        val key = watchService.poll()
        if (key != null && key == watchKey) {
            val events = key.pollEvents()
            for (event in events) {
                println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".")
            }
        }
        val isKeyValid = watchKey.reset()
    } while (isKeyValid)

}
