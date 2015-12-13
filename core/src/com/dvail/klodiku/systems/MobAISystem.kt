package com.dvail.klodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Circle
import com.dvail.klodiku.combat.advanceAttackState
import com.dvail.klodiku.combat.attackingStates
import com.dvail.klodiku.combat.initAttack
import com.dvail.klodiku.entities.BaseState
import com.dvail.klodiku.entities.CompMapper
import com.dvail.klodiku.entities.Comps
import com.dvail.klodiku.entities.MobState
import com.dvail.klodiku.events.EventQueue
import com.dvail.klodiku.pathfinding.AStar
import com.dvail.klodiku.util.*
import java.util.*


class MobAISystem(eventQ: EventQueue) : CoreSystem(eventQ) {
    private val SIGHT_DISTANCE = 300f
    private val ATTACK_RANGE = 50f
    private val WANDER_DISTANCE = 800

    lateinit var world: Engine
    lateinit var player: Entity
    var delta = 0f
    val random = Random()

    override fun addedToEngine(engine: Engine) {
        world = engine
        player = firstEntityWithComp(world, Comps.Player)
    }

    override fun update(sysDelta: Float) {
        delta = sysDelta

        val mobs = entitiesWithComps(world, Comps.MobAI)

        mobs.forEach { mob ->
            val baseState = CompMapper.State.get(mob).current

            if (baseState == BaseState.Standing || baseState == BaseState.Walking) {
                processAI(mob)
            } else if (attackingStates.contains(baseState)) {
                advanceAttackState(delta, mob)
            }
        }
    }

    private fun processAI(entity: Entity) {
        val mobAI = CompMapper.MobAI.get(entity)

        if (mobAI.lastUpdate > mobAI.thinkSpeed) {
            mobAI.lastUpdate = 0f
            updateBehavior(entity)
        } else {
            mobAI.lastUpdate += delta
        }

        actForState(entity, mobAI.state)
    }

    private fun actForState(entity: Entity, state: MobState) {
        when (state) {
            MobState.Wander -> { doWander(entity) }
            MobState.Aggro -> { doAggro(entity) }
        }
    }

    private fun updateBehavior(entity: Entity) {
        val mobAI = CompMapper.MobAI.get(entity)
        when (mobAI.state) {
            MobState.Wander -> {
                setPathTo(entity, randomWanderLocation(entity))
            }
            MobState.Aggro -> {
                mobAI.path = linkedListOf()
            }
            else -> {}
        }

    }

    private fun doWander(entity: Entity) {
        if (!CompMapper.MobAI.get(entity).path.isEmpty()) {
            navigatePath(world, delta, entity)
        }
    }

    private fun doAggro(entity: Entity) {
        if (CompMapper.State.get(player).current == BaseState.Dead) {
            CompMapper.MobAI.get(entity).state = MobState.Wander
        } else if (distanceBetween(entity, player) < SIGHT_DISTANCE) {
            pursuePlayer(entity)
            attackPlayer(entity)
        }
    }

    private fun pursuePlayer(entity: Entity) {
        if (!CompMapper.MobAI.get(entity).path.isEmpty()) {
            navigatePath(world, delta, entity)
        } else {
            setPathTo(entity, CompMapper.Spatial.get(player).pos)
        }
    }

    private fun attackPlayer(entity: Entity) {
        if (distanceBetween(entity, player) < ATTACK_RANGE) initAttack(entity)
    }

    private fun setPathTo(fromEntity: Entity, targetPos: Circle) {
        val worldMap = firstEntityWithComp(world, Comps.WorldMap)
        val currPos = CompMapper.Spatial.get(fromEntity).pos
        val currLocation = AStar.Node((currPos.x / MAP_TILE_SIZE).toInt(), (currPos.y / MAP_TILE_SIZE).toInt())
        val targetLocation = AStar.Node((targetPos.x / MAP_TILE_SIZE).toInt(), (targetPos.y / MAP_TILE_SIZE).toInt())

        val path = AStar.findPath(CompMapper.WorldMap.get(worldMap).grid, currLocation, targetLocation)

        CompMapper.MobAI.get(fromEntity).path = path
    }

    private fun randomWanderLocation(entity: Entity) : Circle {
        val currPos = CompMapper.Spatial.get(entity).pos
        val newX = currPos.x + (random.nextInt(WANDER_DISTANCE * 2) - WANDER_DISTANCE)
        val newY = currPos.y + (random.nextInt(WANDER_DISTANCE * 2) - WANDER_DISTANCE)
        return Circle(Math.abs(newX), Math.abs(newY), 0f)
    }
}