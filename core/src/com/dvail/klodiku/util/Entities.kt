package com.dvail.klodiku.util

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray

class NoValidEntityException : Exception()

fun entitiesWithComps(world: Engine, vararg compTypes: Class<out Component>): ImmutableArray<Entity> {
    val worldFamily = Family.all(*compTypes).get()
    return world.getEntitiesFor(worldFamily)
}

fun entitiesWithCompsExcluding(world: Engine, compsAll: Array<Class<out Component>>,
                     compsNot: Array<Class<out Component>>) : ImmutableArray<Entity> {
    val family = Family.all(*compsAll).exclude(*compsNot).get()
    return world.getEntitiesFor(family)
}

fun firstEntityWithComp(world: Engine, compType: Class<out Component>): Entity {
    val entities = entitiesWithComps(world, compType)

    if (entities.size() <= 0) throw NoValidEntityException()

    return entities.first()
}

@Suppress("SENSELESS_COMPARISON")
fun hasComp(entity: Entity, compType: Class<out Component>): Boolean {
    return entity.getComponent(compType) != null
}