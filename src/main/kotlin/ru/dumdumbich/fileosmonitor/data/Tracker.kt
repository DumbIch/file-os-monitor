package ru.dumdumbich.fileosmonitor.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.dumdumbich.fileosmonitor.domain.Event
import ru.dumdumbich.fileosmonitor.domain.Events
import java.nio.file.*

/**
 * <h3>file-os-monitor</h3>
 * @description
 * <p>Path tracker</p>
 * @author DumbIch
 * @date 2023-10-09 14:30
 **/

class Tracker(private val trackingPath: String, override var logDirectoryPath: String, ) : Logger {

    private var watchService: WatchService = FileSystems.getDefault().newWatchService()
    private var watchKey: WatchKey = Paths.get(trackingPath).register(
        watchService,
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY,
        StandardWatchEventKinds.ENTRY_DELETE
    )

    private val eventsQueue = mutableListOf<Event>()

    private fun translateWatchServiceEvent(event: WatchEvent<*>): Event = when (event.kind()) {
        StandardWatchEventKinds.ENTRY_CREATE -> {
            Event(
                Events.CREATE,
                event.context().toString(),
                event.count()
            ) {} // { println("Event CREATE [Path : ${event.context()}  Count : ${event.count()} ]") }
        }

        StandardWatchEventKinds.ENTRY_MODIFY -> {
            Event(
                Events.MODIFY,
                event.context().toString(),
                event.count()
            ) {} // { println("Event MODIFY [Path : ${event.context()}  Count : ${event.count()} ]") }
        }

        StandardWatchEventKinds.ENTRY_DELETE -> {
            Event(
                Events.DELETE,
                event.context().toString(),
                event.count()
            ) {} // { println("Event DELETE [Path : ${event.context()}  Count : ${event.count()} ]") }
        }

        else -> {
            Event.undefined()
        }
    }

    private suspend fun getWatchEvents(): List<WatchEvent<*>> {
        val key = withContext(Dispatchers.IO) {
            watchService.take()
        }
        var watchEvents: List<WatchEvent<*>> = emptyList()
        if (key != null && key == watchKey) {
            do {
                watchEvents = key.pollEvents()
            } while (watchEvents.isEmpty())
        }
        return watchEvents
    }

    private var previousWatchEvent: WatchEvent<*>? = null
    private fun getLastEvents(watchEvents: List<WatchEvent<*>>): List<Event> {
        val lastEventsList = mutableListOf<Event>()
        for (event in watchEvents) {
            if (previousWatchEvent != null) {
                if (previousWatchEvent!!.kind() == event.kind() && previousWatchEvent!!.context() == event.context()) {
                    continue
                }
            }
            lastEventsList.add(translateWatchServiceEvent(event))
            previousWatchEvent = event
        }
        return lastEventsList
    }

    suspend fun updateEvents(): Boolean {
        val watchEvents = getWatchEvents()
        eventsQueue.addAll(getLastEvents(watchEvents))
        return watchKey.reset()
    }

    suspend fun getEvent(): Event {
        while (eventsQueue.isEmpty()) {
            delay(100L)
        }
        val event = eventsQueue.removeAt(0)
        fileMsg("Event : $event")
        return event
    }

 }
