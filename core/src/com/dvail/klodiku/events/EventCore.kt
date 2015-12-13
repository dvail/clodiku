package com.dvail.klodiku.events

import com.badlogic.ashley.core.Engine
import java.util.*

enum class EventType {
    Combat, UI
}

interface Event {
    // Updates the event state. Returns true if the event has expired and should be removed.
    fun processEvent(world: Engine, delta: Float): Boolean
}

class EventQueue {
    val events = HashMap<EventType, LinkedList<Event>>();

    init {
        EventType.values().forEach { events.put(it, LinkedList()) }
    }

    fun addEvent(type: EventType, event: Event) {
        events[type]?.add(event)
    }

    // TODO Is remove an O(n) operation here? Shouldn't be too bad if the list size is small
    fun clearEvent(type: EventType, event: Event) {
        events[type]?.remove(event)
    }

    fun popEvent(type:EventType) {
        events[type]?.pop()
    }

    fun update(world: Engine, delta: Float) {
        events.keys.forEach { updateEvents(world, delta, it) }
    }

    // TODO Be careful with ConcurrentModificationExceptions here
    private fun updateEvents(world: Engine, delta: Float, type: EventType) {
        events[type]?.forEach { event ->
            if (event.processEvent(world, delta)) {
                clearEvent(type, event)
            }
        }
    }
}

