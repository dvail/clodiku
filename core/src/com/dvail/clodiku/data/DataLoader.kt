package com.dvail.clodiku.data

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.ComponentFactory
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.entities.EqSlot
import com.moandjiezana.toml.Toml
import java.io.File
import java.util.*

//TODO Define a "conventions" file for how TOML data files should look
class DataLoader() {

    val compStringMap = HashMap<String, Class<out Component>>()

    init {
        compStringMap.put("State", Comps.State)
        compStringMap.put("MobAI", Comps.MobAI)
        compStringMap.put("Spatial", Comps.Spatial)
        compStringMap.put("Renderable", Comps.Renderable)
        compStringMap.put("AnimatedRenderable", Comps.AnimatedRenderable)
        compStringMap.put("Attribute", Comps.Attribute)
        compStringMap.put("Item", Comps.Item)
        compStringMap.put("EqItem", Comps.EqItem)
        compStringMap.put("EqWeapon", Comps.EqWeapon)
        compStringMap.put("EqArmor", Comps.EqArmor)
        compStringMap.put("Equipment", Comps.Equipment)
        compStringMap.put("Inventory", Comps.Inventory)
    }

    fun loadArea(world: Engine, areaName: String) {
        val areaData = File("./maps/$areaName/data.toml")
        val areaToml = Toml().read(areaData)

        loadFreeItems(world, areaToml.getTables("free-item"))
        loadMobs(world, areaToml.getTables("mob"))
    }

    private fun loadFreeItems(world: Engine, items: List<Toml>?) {
        if (items != null) {
            val freeItemEntities = buildEntities(items)
            freeItemEntities.forEach { world.addEntity(it) }
        }
    }

    private fun loadMobs(world: Engine, mobs: List<Toml>) {
        mobs.forEach { mob ->
            val mobComps = buildComponentList(mob.getTable("components"))
            val mobInventory = mob.getList<Toml>("inventory").map { buildComponentList(it) }
            val mobEquipment = buildMobEq(mob.getTable("equipment"))

            val mobEntity = Entity()
            mobComps.forEach { mobEntity.add(it) }

            val items = mobInventory.map { comps ->
                val item = Entity()
                comps.forEach { comp -> item.add(comp) }
                item
            }

            items.forEach { item ->
                world.addEntity(item)
                CompMapper.Inventory.get(mobEntity).items.add(item)
            }

            mobEquipment.values.forEach { world.addEntity(it) }
            CompMapper.Equipment.get(mobEntity).items = mobEquipment

            world.addEntity(mobEntity)
        }
    }

    private fun buildEntities(items: List<Toml>): List<Entity> {
        val itemComponentLists = items.map { buildComponentList(it.getTable("components")) }

        return itemComponentLists.map { it ->
            val item = Entity()
            it.forEach { comp -> item.add(comp) }
            item
        }
    }

    private fun buildComponentList(table: Toml) : List<Component> {
        return table.entrySet().map { ComponentFactory.createComponent(compStringMap[it.key], it.value as Toml) }
    }

    private fun buildMobEq(toml: Toml) : HashMap<EqSlot, Entity> {
        val mobEq = HashMap<EqSlot, Entity>()

        toml.entrySet().forEach { entry ->
            val entity = Entity()
            buildComponentList(entry.value as Toml).forEach { entity.add(it) }
            mobEq.put(EqSlot.valueOf(entry.key), entity)
        }

        return mobEq
    }

}