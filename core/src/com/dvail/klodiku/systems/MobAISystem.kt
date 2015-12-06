package com.dvail.klodiku.systems

import com.badlogic.ashley.core.Engine
import com.dvail.klodiku.events.EventQueue

class MobAISystem(eventQ: EventQueue) : CoreSystem(eventQ) {

    lateinit var world: Engine
    var delta = 0f

    override fun addedToEngine(engine: Engine) {
        world = engine
    }

    override fun update(sysDelta: Float) {
        delta = sysDelta
    }
}