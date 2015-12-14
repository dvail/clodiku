package com.dvail.klodiku.events

import com.badlogic.ashley.core.Engine
import java.util.*

enum class EventType {
    Combat, UI, World
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

    fun popEvent(type:EventType) {
        events[type]?.pop()
    }

    fun update(world: Engine, delta: Float) {
        events.keys.forEach { updateEvents(world, delta, it) }
    }

    private fun updateEvents(world: Engine, delta: Float, type: EventType) {
        val iterator = events[type]?.iterator() ?: return

        while (iterator.hasNext()) {
            if (iterator.next().processEvent(world, delta)) {
                iterator.remove()
            }
        }
    }
}

