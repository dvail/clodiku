package com.dvail.klodiku.events

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.klodiku.entities.CompMapper

data class UnequipItemEvent(val entity: Entity, val itemEntity: Entity): Event {

    override fun processEvent(world: Engine, delta: Float): Boolean {
        val slot = CompMapper.EqItem.get(itemEntity).slot

        CompMapper.Equipment.get(entity).items.remove(slot)
        CompMapper.Inventory.get(entity).items.add(itemEntity)

        return true
    }
}

data class EquipItemEvent(val entity: Entity, val itemEntity: Entity): Event {
    override fun processEvent(world: Engine, delta: Float): Boolean {
        val slot = CompMapper.EqItem.get(itemEntity).slot
        val eq = CompMapper.Equipment.get(entity).items
        val inv = CompMapper.Inventory.get(entity).items

        val oldItem = eq.remove(slot)
        if (oldItem != null) inv.add(oldItem)

        inv.remove(itemEntity)
        eq.put(slot, itemEntity)

        return true
    }
}

data class DropItemEvent(val entity: Entity, val itemEntity: Entity): Event {
    override fun processEvent(world: Engine, delta: Float): Boolean {
        return true
    }
}