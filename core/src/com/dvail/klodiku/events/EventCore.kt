package com.dvail.klodiku.events

import com.badlogic.ashley.core.Engine
import java.util.*

enum class EventType {
    Combat, UI
}

interface Event {
    fun processEvent(world: Engine)
}

class EventQueue {
    val events = HashMap<EventType, LinkedList<Event>>();

    init {
        EventType.values().forEach { events.put(it, LinkedList()) }
    }

    fun addEvent(type: EventType, event: Event) {
        events[type]?.add(event)
    }

    fun clearEvent(type: EventType, event: Event) {
        events[type]?.remove(event)
    }

    fun popEvent(type:EventType) {
        events[type]?.pop()
    }
}

