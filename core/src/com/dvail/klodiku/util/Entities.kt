package com.dvail.klodiku.util

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray

fun entitiesWithComps(world: Engine, vararg compTypes: Class<out Component>): ImmutableArray<Entity> {
    val worldFamily = Family.all(*compTypes).get()
    return world.getEntitiesFor(worldFamily)
}

fun firstEntityWithComp(world: Engine, compType: Class<out Component>): Entity? {
    val entities = entitiesWithComps(world, compType)
    return if (entities.size() > 0) entities.first() else null
}

fun compData(entity: Entity, compMapper: ComponentMapper<out Component>) = compMapper.get(entity)