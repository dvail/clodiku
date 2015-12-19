package com.dvail.clodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.combat.advanceAttackState
import com.dvail.clodiku.combat.initAttack
import com.dvail.clodiku.entities.BaseState
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.entities.State
import com.dvail.clodiku.events.EventQueue
import com.dvail.clodiku.util.*
import com.dvail.clodiku.world.GameEngine

class InputSystem(eventQ: EventQueue) : CoreSystem(eventQ) {

    lateinit var world: GameEngine
    lateinit var player: Entity
    var delta = 0f

    lateinit var playerState: State

    override fun addedToEngine(engine: Engine) {
        world = engine as GameEngine
        player = firstEntityWithComp(world, Comps.Player)
        playerState = CompMapper.State.get(player)
    }

    override fun update(sysDelta: Float) {
        if (keyJustPressed(BoundKeys.Pause)) world.paused = !world.paused
        if (world.paused) return

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
