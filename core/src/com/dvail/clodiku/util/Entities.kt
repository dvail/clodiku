package com.dvail.clodiku.util

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.dvail.clodiku.entities.Carried
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.entities.DisposableComponent
import java.util.*

class NoValidEntityException : Exception()

object Entities {
    fun getFreeItems(world: Engine): List<Entity> {
        return entitiesWithComps(world, Comps.Spatial, Comps.Item).filter {
            CompMapper.Spatial.get(it).pos != Carried
        }
    }
}

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

fun destroyNonPlayerEntities(world: Engine) {
    val safeEntities = ArrayList<Entity>()
    val player = firstEntityWithComp(world, Comps.Player)

    safeEntities.add(player)
    safeEntities.addAll(CompMapper.Inventory.get(player).items)
    safeEntities.addAll(CompMapper.Equipment.get(player).items.values)

    world.entities.forEach {
        if (!safeEntities.contains(it)) {
            disposeComponents(it.components)
            world.removeEntity(it)
        }
    }
}

private fun disposeComponents(comps: ImmutableArray<Component>) {
    comps.forEach {
        if (it is DisposableComponent) {
            it.dispose()
        }
    }
}