package ru.dumdumbich.fileosmonitor.domain

/**
 * <h3>file-os-monitor</h3>
 * @description
 * <p>File OS Event</p>
 * @author DumbIch
 * @date 2023-10-09 14:27
 **/

enum class Events() {
    CREATE,
    CREATED,
    MODIFY,
    MODIFIED,
    DELETE,
    DELETED,
    UNDEFINED,
    FAIL,
    NON_EXISTENT,
}

data class Event(val type: Events, val path: String, val count: Int, val action: () -> Unit) {
    companion object {
        fun empty() = Event(Events.NON_EXISTENT, "", 0) { println("Event non existent") }
        fun undefined() = Event(Events.UNDEFINED, "", 0) { println("Event non defined") }
    }

}
