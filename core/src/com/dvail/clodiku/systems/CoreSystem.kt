package com.dvail.clodiku.systems

import com.badlogic.ashley.core.EntitySystem
import com.dvail.clodiku.events.EventQueue

open class CoreSystem() : EntitySystem() {
    lateinit var eventQ: EventQueue

    constructor(sysEvents: EventQueue) : this() {
        eventQ = sysEvents
    }
}
