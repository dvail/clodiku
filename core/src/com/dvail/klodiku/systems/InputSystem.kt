package com.dvail.klodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.dvail.klodiku.combat.advanceAttackState
import com.dvail.klodiku.combat.initAttack
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.util.*

class InputSystem : EntitySystem() {

    lateinit var world: Engine
    lateinit var player: Entity

    lateinit var playerState: State

    override fun addedToEngine(engine: Engine) {
        world = engine;
        player = firstEntityWithComp(world, Comps.Player)
        playerState = compData(player, CompMapper.State) as State
    }

    override fun update(delta: Float) {
        when (playerState.current) {
            BaseState.Walking -> doFreeInput(delta)
            BaseState.Standing -> doFreeInput(delta)
            BaseState.Melee_Pierce -> doCombatInput(delta)
            BaseState.Melee_Slash -> doCombatInput(delta)
        }
    }

    private fun doFreeInput(delta: Float) {
        if (keyJustPressed(BoundKeys.MeleeAttack)) {
            initAttack(world, delta, player)
        } else {
            movePlayer(delta)
        }
    }

    private fun doCombatInput(delta: Float) {
        advanceAttackState(world, delta, player)
    }

    private fun movePlayer(delta: Float) {
        val moveX = if (keyPressed(BoundKeys.MoveEast)) 2f else if (keyPressed(BoundKeys.MoveWest)) -2f else 0f
        val moveY = if (keyPressed(BoundKeys.MoveNorth)) 2f else if (keyPressed(BoundKeys.MoveSouth)) -2f else 0f

        moveEntity(world, delta, player, moveX, moveY)
    }
}
