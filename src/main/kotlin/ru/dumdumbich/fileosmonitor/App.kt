package ru.dumdumbich.fileosmonitor

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.dumdumbich.fileosmonitor.data.Tracker
import ru.dumdumbich.fileosmonitor.data.Tuner
import ru.dumdumbich.fileosmonitor.domain.Event
import ru.dumdumbich.fileosmonitor.domain.Events


fun main(args: Array<String>) {

    val tuner = Tuner(args)
    val logDirectory = tuner.loggerPathString
    val trackerBaseDirectory = tuner.trackingPathString
//    val a3TrackerDirectory = "$trackerBaseDirectory/a3"
    val tracker = Tracker(trackerBaseDirectory, logDirectory)

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val eventsFlow = MutableSharedFlow<Event>()

    eventsFlow.onEach { event ->
        event.action()
        if (event.type == Events.FAIL) {
            coroutineScope.cancel("Application terminated")
        }
    }.launchIn(coroutineScope)

    coroutineScope.launch {
        do {
            delay(500L)
            val event = tracker.getEvent()
            eventsFlow.emit(event)
        } while (isActive)
    }

    coroutineScope.launch {
        do {
            delay(500L)
            tracker.updateEvents()
        } while (isActive)
    }

    while (coroutineScope.isActive) {
    }

}
