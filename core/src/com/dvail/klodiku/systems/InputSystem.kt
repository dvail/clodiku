package com.dvail.klodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.util.*

class InputSystem : EntitySystem() {

    lateinit var world: Engine
    lateinit var player: Entity

    lateinit var playerState: State
    lateinit var playerSpatial: Spatial

    override fun addedToEngine(engine: Engine) {
        world = engine;
        player = firstEntityWithComp(world, Comps.Player)
        playerState = compData(player, CompMapper.State) as State
        playerSpatial = compData(player, CompMapper.Spatial) as Spatial
    }

    override fun update(delta: Float) {
        when (playerState.current) {
            PlayerState.Walking -> doFreeInput()
            PlayerState.Idle -> doFreeInput()
            PlayerState.Melee -> advanceAttackState()
        }
    }

    fun doFreeInput() {
        movePlayer()
    }

    fun advanceAttackState() {

    }

    fun movePlayer() {
        val moveX = if (keyPressed(BoundKeys.MoveEast)) 2f else if (keyPressed(BoundKeys.MoveWest)) -2f else 0f
        val moveY = if (keyPressed(BoundKeys.MoveNorth)) 2f else if (keyPressed(BoundKeys.MoveSouth)) -2f else 0f

        moveEntity(playerSpatial, moveX, moveY)
    }
}
