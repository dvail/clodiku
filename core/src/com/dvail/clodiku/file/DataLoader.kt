package com.dvail.clodiku.file

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.ComponentFactory
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.entities.EqSlot
import com.dvail.clodiku.world.GameEngine
import com.moandjiezana.toml.Toml
import java.io.File
import java.util.*

//TODO Define a "conventions" file for how TOML file files should look
class DataLoader() {
    private val REPOP_LIMIT = 300.0 // Five minutes

    val compStringMap = HashMap<String, Class<out Component>>()

    init {
        compStringMap.put("Player", Comps.Player)
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
        compStringMap.put("Martial", Comps.Martial)
    }

    fun savedPlayerArea(saveLocation: String) : String {
        val playerToml = Toml().read(File("$saveLocation/PLAYER.toml"))

        return playerToml.getString("area")
    }

    // If enough time has passed since the area was saved, load the original file as a repop
    // TODO Need to find a more robust way to handle partial repops.
    fun loadArea(world: Engine, saveLocation: String, areaName: String) {
        world as GameEngine
        val savedAreaFile = File("$saveLocation/$areaName.toml")
        val defaultAreaFile = File("./maps/$areaName/data.toml")

        val areaToml = if (savedAreaFile.exists()) {
            val toml = Toml().read(savedAreaFile)
            val lastSave = toml.getDouble("lastSave") ?: 0.0

            if (Math.abs(lastSave - world.gameTime) > REPOP_LIMIT) {
                println("Repop area")
                Toml().read(defaultAreaFile)
            } else {
                toml
            }
        } else {
            Toml().read(defaultAreaFile)
        }

        loadFreeItems(world, areaToml.getTables("free-item"))

        val mobTomls = areaToml.getTables("mob")
        mobTomls.forEach { loadCharacter(world, it) }
    }

    fun loadPlayer(world: Engine, saveLocation: String) {
        val playerToml = Toml().read(File("$saveLocation/PLAYER.toml"))

        (world as GameEngine).gameTime = playerToml.getDouble("gameTime") ?: 0.0
        loadCharacter(world, playerToml)
    }

    private fun loadFreeItems(world: Engine, items: List<Toml>?) {
        if (items != null) {
            val freeItemEntities = buildEntities(items)
            freeItemEntities.forEach { world.addEntity(it) }
        }
    }

    private fun loadCharacter(world: Engine, entityToml: Toml) {
        val components = buildComponentList(entityToml.getTable("components"))

        val inventory = entityToml.getTables("inventory").map {
            buildComponentList(it as Toml)
        }
        val equipment = buildEntityEq(entityToml.getTable("equipment"))

        val entity = Entity()
        components.forEach { entity.add(it) }

        val items = inventory.map { comps ->
            val item = Entity()
            comps.forEach { comp -> item.add(comp) }
            item
        }

        items.forEach { item ->
            world.addEntity(item)
            CompMapper.Inventory.get(entity).items.add(item)
        }

        equipment.values.forEach { world.addEntity(it) }
        CompMapper.Equipment.get(entity).items = equipment

        world.addEntity(entity)
    }

    private fun buildEntities(items: List<Toml>): List<Entity> {
        val itemComponentLists = items.map { buildComponentList(it.getTable("components")) }

        return itemComponentLists.map { it ->
            val item = Entity()
            it.forEach { comp -> item.add(comp) }
            item
        }
    }

    private fun buildComponentList(table: HashMap<String, Toml>) : List<Component> {
        return table.entries.map { ComponentFactory.createComponent(compStringMap[it.key], it.value) }
    }

    private fun buildComponentList(table: Toml) : List<Component> {
        return table.entrySet().map { ComponentFactory.createComponent(compStringMap[it.key], it.value as Toml) }
    }

    private fun buildEntityEq(toml: Toml) : HashMap<EqSlot, Entity> {
        val entityEq = HashMap<EqSlot, Entity>()

        toml.entrySet().forEach { entry ->
            val entity = Entity()
            buildComponentList(entry.value as Toml).forEach { entity.add(it) }
            entityEq.put(EqSlot.valueOf(entry.key), entity)
        }

        return entityEq
    }

}