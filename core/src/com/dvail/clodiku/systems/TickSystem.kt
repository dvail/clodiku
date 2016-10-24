package com.dvail.clodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.combat.regenEntity
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.events.EventQueue
import com.dvail.clodiku.util.Entities
import com.dvail.clodiku.world.GameEngine

/**
 * Created by dave on 10/23/16.
 */
class TickSystem(eventQ: EventQueue) : CoreSystem(eventQ) {

    val tickAvgLengthSec = 20

    lateinit var world: GameEngine
    lateinit var player: Entity
    var delta = 0f
    var nextTick = 20f

    override fun addedToEngine(engine: Engine) {
        world = engine as GameEngine
        player = Entities.firstWithComp(world, Comps.Player)
    }

    override fun update(sysDelta: Float) {
        if (world.paused) return

        delta += sysDelta

        if (delta > nextTick) doTick()
    }

    private fun doTick() {
        println("Tock")
        nextTick = delta + (tickAvgLengthSec * (1.5 - Math.random()).toFloat())
        doRegen()
    }

    private fun doRegen() {
        val regenEntities = Entities.withComps(world, Comps.Attribute)

        regenEntities.forEach(::regenEntity)
    }
}