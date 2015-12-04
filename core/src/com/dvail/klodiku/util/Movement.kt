package com.dvail.klodiku.util

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.dvail.klodiku.entities.*
import java.util.*

fun moveEntity(world: Engine, delta: Float, entity: Entity, movX: Float, movY: Float) {
    updateState(delta, entity, movX, movY)
    updateSpatial(world, entity, movX, movY)
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
    var currSpatial: Spatial
    val newPosition = Circle(spatial.pos.x + movX, spatial.pos.y + movY, spatial.pos.radius)

    val collidesWithEntity = collisionEntities.any { it ->
        currSpatial = CompMapper.Spatial.get(it)
        return Intersector.overlaps(newPosition, currSpatial.pos)
    }

    val collidesWithMap = mapObjects.any { it ->
        Intersector.overlaps(newPosition, (it as RectangleMapObject).rectangle)
    }

    return collidesWithEntity || collidesWithMap
}
