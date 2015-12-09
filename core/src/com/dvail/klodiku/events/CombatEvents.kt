package com.dvail.klodiku.events

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Circle

data class EventMeleeHit(val attacker: Entity, val defender: Entity, val location: Circle, val damage: Int): Event {
    var time: Float = 0f

    override fun processEvent(world: Engine, delta: Float): Boolean {
        return false
    }
}