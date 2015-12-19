package com.dvail.clodiku.systems

import com.badlogic.ashley.core.Engine
import com.dvail.clodiku.events.EventQueue
import com.dvail.clodiku.world.GameEngine

class EventSystem(eventQ: EventQueue) : CoreSystem(eventQ) {

    lateinit var world: GameEngine
    var delta = 0f

    override fun addedToEngine(engine: Engine) {
        world = engine as GameEngine
    }

    override fun update(sysDelta: Float) {
        if (world.paused) return

        delta = sysDelta
        eventQ.update(world, delta)
    }
}