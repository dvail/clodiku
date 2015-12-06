package com.dvail.klodiku.systems

import com.badlogic.ashley.core.EntitySystem
import com.dvail.klodiku.events.EventQueue

open class CoreSystem() : EntitySystem() {
    lateinit var eventQ: EventQueue

    constructor(sysEvents: EventQueue) : this() {
        eventQ = sysEvents
    }
}
