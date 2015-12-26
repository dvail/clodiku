package com.dvail.clodiku.util

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.dvail.clodiku.entities.*
import com.dvail.clodiku.events.EventQueue
import com.dvail.clodiku.events.EventType
import com.dvail.clodiku.events.SwapAreaEvent
import com.dvail.clodiku.pathfinding.AStar
import com.dvail.clodiku.world.Maps

object Movement {

    fun moveEntity(world: Engine, delta: Float, entity: Entity, movX: Float, movY: Float) {
        updateState(delta, entity, movX, movY)
        updateSpatial(world, entity, movX, movY)
    }

    fun moveMob(world: Engine, delta: Float, entity: Entity, targetX: Float, targetY: Float) {
        val currPos = CompMapper.Spatial.get(entity).pos
        val dX = Math.abs(targetX - currPos.x)
        val dY = Math.abs(targetY - currPos.y)

        //TODO Replace 2f with actual entity movement speed
        var movX = Math.min(dX, 2f)
        var movY = Math.min(dY, 2f)

        if (targetX < currPos.x) movX *= -1
        if (targetY < currPos.y) movY *= -1

        moveEntity(world, delta, entity, movX, movY)
    }

    fun entityDirection(x: Float, y: Float): Direction {
        return when (true) {
            x > 0 -> Direction.East
            x < 0 -> Direction.West
            y > 0 -> Direction.North
            else -> Direction.South
        }
    }

    fun getEntityCollisions(hitBox: Circle, defenders: List<Entity>): Set<Entity> {
        return defenders.filter { Intersector.overlaps(hitBox, CompMapper.Spatial.get(it).pos) }.toHashSet()
    }

    fun navigatePath(world: Engine, delta: Float, entity: Entity) {
        val path = CompMapper.MobAI.get(entity).path
        val currPos = CompMapper.Spatial.get(entity).pos
        val (targetPosX, targetPosY) = nodeToPixel(path.last())

        if (Math.abs(currPos.x - targetPosX) < 2 && Math.abs(currPos.y - targetPosY) < 2) {
            path.removeLast()
        } else {
            moveMob(world, delta, entity, targetPosX, targetPosY)
        }
    }

    fun nodeToPixel(node: AStar.Node): Pair<Float, Float> {
        return Pair((node.x * Maps.MAP_TILE_SIZE) + Maps.HALF_TILE_SIZE, (node.y * Maps.MAP_TILE_SIZE) + Maps.HALF_TILE_SIZE)
    }

    fun distanceBetween(entityA: Entity, entityB: Entity): Float {
        val posA = CompMapper.Spatial.get(entityA).pos
        val posB = CompMapper.Spatial.get(entityB).pos
        return Math.abs(posA.x - posB.x) + Math.abs(posA.y - posB.y)
    }

    fun grabItem(world: Engine, entity: Entity) {
        val entityPos = CompMapper.Spatial.get(entity).pos

        val targetItem = Entities.getFreeItems(world).filter {
            Intersector.overlaps(CompMapper.Spatial.get(it).pos, entityPos)
        }.firstOrNull()

        if (targetItem != null) {
            CompMapper.Inventory.get(entity).items.add(targetItem)
            CompMapper.Spatial.get(targetItem).pos = Carried.copy()
        }
    }

    fun attemptAreaTransport(world: Engine, eventQ: EventQueue, player: Entity, movX: Float, movY: Float) {
        val mapTransportZones = Maps.getTransportZones(world)
        val playerPos = CompMapper.Spatial.get(player).pos
        val newPosition = Circle(playerPos.x + movX, playerPos.y + movY, playerPos.radius)

        val transportZone = mapTransportZones.firstOrNull { it ->
            Intersector.overlaps(newPosition, (it as RectangleMapObject).rectangle)
        }

        if (transportZone != null) {
            eventQ.addEvent(EventType.World, SwapAreaEvent(transportZone))
        }
    }

    private fun updateState(delta: Float, entity: Entity, movX: Float, movY: Float) {
        val state = CompMapper.State.get(entity)
        val oldState = state.current.name

        state.current = if (movX != 0f || movY != 0f) BaseState.Walking else BaseState.Standing
        state.time = if (oldState == state.current.name) state.time + delta else 0f
    }

    private fun updateSpatial(world: Engine, entity: Entity, movX: Float, movY: Float) {
        val entitySpatial = CompMapper.Spatial.get(entity)
        val mapObstacles = Maps.mapObstacles(world)
        val collisionEntities = Entities.withCompsExcluding(world, Array(1, { Comps.Spatial }), Array(1, { Comps.Item }))
        val livingEntities = collisionEntities.filter { CompMapper.State.get(it)?.current != BaseState.Dead }
        val otherEntities = livingEntities.filter { it -> it != entity }

        if (!collision(entitySpatial, mapObstacles, otherEntities, movX, 0f)) {
            entitySpatial.pos.x += movX
        }

        if (!collision(entitySpatial, mapObstacles, otherEntities, 0f, movY)) {
            entitySpatial.pos.y += movY
        }

        if (movX != 0f || movY != 0f) entitySpatial.direction = entityDirection(movX, movY)
    }

    private fun collision(spatial: Spatial, mapObjects: MapObjects,
                          collisionEntities: List<Entity>, movX: Float, movY: Float): Boolean {
        val newPosition = Circle(spatial.pos.x + movX, spatial.pos.y + movY, spatial.pos.radius)

        val collidesWithEntity = collisionEntities.any { it ->
            Intersector.overlaps(newPosition, CompMapper.Spatial.get(it).pos)
        }

        val collidesWithMap = mapObjects.any { it ->
            Intersector.overlaps(newPosition, (it as RectangleMapObject).rectangle)
        }

        return collidesWithEntity || collidesWithMap
    }

}
