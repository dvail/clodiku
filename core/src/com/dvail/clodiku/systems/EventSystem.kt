package com.dvail.clodiku.systems

import com.badlogic.ashley.core.Engine
import com.dvail.clodiku.events.EventQueue

class EventSystem(eventQ: EventQueue) : CoreSystem(eventQ) {

    lateinit var world: Engine
    var delta = 0f

    override fun addedToEngine(engine: Engine) {
        world = engine
    }

    override fun update(sysDelta: Float) {
        delta = sysDelta
        eventQ.update(world, delta)
    }
}