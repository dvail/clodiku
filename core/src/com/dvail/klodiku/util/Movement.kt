package com.dvail.klodiku.util

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.pathfinding.AStar
import java.util.*

fun moveEntity(world: Engine, delta: Float, entity: Entity, movX: Float, movY: Float) {
    updateState(delta, entity, movX, movY)
    updateSpatial(world, entity, movX, movY)
}

fun moveMob(world: Engine, delta: Float,entity: Entity, target: AStar.Node) {
    val currPos = CompMapper.Spatial.get(entity).pos
    val pixelX = (target.x * MAP_TILE_SIZE) + HALF_TILE_SIZE
    val pixelY = (target.y * MAP_TILE_SIZE) + HALF_TILE_SIZE
    val dX = Math.abs(pixelX - currPos.x)
    val dY = Math.abs(pixelY - currPos.y)

    var movX = Math.min(dX, 2f)
    var movY = Math.min(dY, 2f)

    if (pixelX > currPos.x) movX *= -1
    if (pixelY > currPos.y) movY *= -1

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

fun getEntityCollisions(hitBox: Circle, defenders: ImmutableArray<Entity>): Set<Entity> {
    return defenders.filter { Intersector.overlaps(hitBox, CompMapper.Spatial.get(it).pos) }.toHashSet()
}

fun navigatePath(world: Engine, delta: Float, entity: Entity) {
    val path = CompMapper.MobAI.get(entity).path
    val currPos = CompMapper.Spatial.get(entity).pos
    val targetPos = path.last()

    if (currPos.x - targetPos.x < 2 && currPos.y - targetPos.y < 2) {
        path.dropLast(1)
    } else {
        moveMob(world, delta, entity, targetPos)
    }
}

fun distanceBetween(entityA: Entity, entityB: Entity) : Float {
    val posA = CompMapper.Spatial.get(entityA).pos
    val posB = CompMapper.Spatial.get(entityB).pos
    return Math.abs(posA.x - posB.x) + Math.abs(posA.y - posB.y)
}

private fun updateState(delta: Float, entity: Entity, movX: Float, movY: Float) {
    val state = CompMapper.State.get(entity)
    val oldState = state.current.name

    state.current = if (movX != 0f || movY != 0f) BaseState.Walking else BaseState.Standing
    state.time = if (oldState == state.current.name) state.time + delta else 0f
}

private fun updateSpatial(world: Engine, entity: Entity, movX: Float, movY: Float) {
    val entitySpatial = CompMapper.Spatial.get(entity)
    val mapObstacles = mapObstacles(world)
    val collisionEntities = entitiesWithCompsExcluding(world, Array(1, { Comps.Spatial }), Array(1, { Comps.Item }))
    val otherEntities = collisionEntities.filter { it -> it != entity }

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
