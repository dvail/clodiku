package com.dvail.klodiku.util

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray

fun entitiesWithComp(world: Engine, compType: Class<out Component>): ImmutableArray<Entity>? {
    val worldFamily = Family.one(compType).get()
    return world.getEntitiesFor(worldFamily)
}

fun firstEntityWithComp(world: Engine, compType: Class<out Component>): Entity? {
    val entities = entitiesWithComp(world, compType)
    return if (entities != null && entities.size() > 0) entities.first() else null
}

fun entityComp(entity: Entity, compMapper: ComponentMapper<out Component>) = compMapper.get(entity)