package com.dvail.clodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.combat.advanceAttackState
import com.dvail.clodiku.combat.initAttack
import com.dvail.clodiku.entities.*
import com.dvail.clodiku.events.EventQueue
import com.dvail.clodiku.util.*

class InputSystem(eventQ: EventQueue) : CoreSystem(eventQ) {

    lateinit var world: Engine
    lateinit var player: Entity
    var delta = 0f

    lateinit var playerState: State

    override fun addedToEngine(engine: Engine) {
        world = engine;
        player = firstEntityWithComp(world, Comps.Player)
        playerState = CompMapper.State.get(player)
    }

    override fun update(sysDelta: Float) {
        delta = sysDelta
        when (playerState.current) {
            BaseState.Walking -> doFreeInput()
            BaseState.Standing -> doFreeInput()
            BaseState.Melee_Pierce -> doCombatInput()
            BaseState.Melee_Slash -> doCombatInput()
            else -> {}
        }
    }

    private fun doFreeInput() {
        if (keyJustPressed(BoundKeys.MeleeAttack)) {
            initAttack(player)
        } else if (keyJustPressed(BoundKeys.GetItem)) {
            Movement.grabItem(world, player)
        } else {
            movePlayer()
        }
    }

    private fun doCombatInput() {
        advanceAttackState(delta, player)
    }

    private fun movePlayer() {
        val moveX = if (keyPressed(BoundKeys.MoveEast)) 2f else if (keyPressed(BoundKeys.MoveWest)) -2f else 0f
        val moveY = if (keyPressed(BoundKeys.MoveNorth)) 2f else if (keyPressed(BoundKeys.MoveSouth)) -2f else 0f

        Movement.moveEntity(world, delta, player, moveX, moveY)
        Movement.attemptAreaTransport(world, eventQ, player, moveX, moveY)
    }
}
