package com.dvail.clodiku.file

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.XmlReader
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.ComponentFactory
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.entities.EqSlot
import com.dvail.clodiku.world.GameEngine
import java.io.File
import java.util.*
import com.badlogic.gdx.utils.Array as GdxArray

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
        val config = XmlReader().parse(FileHandle("$saveLocation/PLAYER.xml"))

        return config.getChildByName("area").getAttribute("name")
    }

    // If enough time has passed since the area was saved, load the original file as a repop
    // TODO Need to find a more robust way to handle partial repops.
    fun loadArea(world: Engine, saveLocation: String, areaName: String) {
        world as GameEngine
        val savedAreaConfig = File("$saveLocation/$areaName.xml")
        val defaultAreaConfig = File("./maps/$areaName/data.xml")

        val areaConfig = if (savedAreaConfig.exists()) {
            val config = XmlReader().parse(FileHandle(savedAreaConfig))
            val lastSave = config.getChildByName("last-save")?.getFloatAttribute("value")?.toDouble() ?: 0.0

            if (Math.abs(lastSave - world.gameTime) > REPOP_LIMIT) {
                println("Repop area")
                XmlReader().parse(FileHandle(defaultAreaConfig))
            } else {
                config
            }
        } else {
            XmlReader().parse(FileHandle(defaultAreaConfig))
        }

        loadFreeItems(world, areaConfig.getChildrenByName("free-item"))

        val mobTomls = areaConfig.getChildrenByName("mob")
        mobTomls.forEach { loadCharacter(world, it) }
    }

    fun loadPlayer(world: Engine, saveLocation: String) {
        val playerConfig = XmlReader().parse(FileHandle("$saveLocation/PLAYER.xml"))

        (world as GameEngine).gameTime = playerConfig.getChildByName("game-time").getFloatAttribute("value").toDouble()
        loadCharacter(world, playerConfig)
    }

    private fun loadFreeItems(world: Engine, items: GdxArray<XmlReader.Element>?) {
        if (items != null) {
            val freeItemEntities = buildEntities(items)
            freeItemEntities.forEach { world.addEntity(it) }
        }
    }

    private fun loadCharacter(world: Engine, entityConfig: XmlReader.Element) {
        val components = buildComponentList(entityConfig.getChildByName("components"))

        val inventory = entityConfig.getChildByName("inventory").getChildrenByName("item").map {
            buildComponentList(it)
        }
        val equipment = buildEntityEq(entityConfig.getChildByName("equipment"))

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

    private fun buildEntities(items: GdxArray<XmlReader.Element>): List<Entity> {
        val itemComponentLists = items.map { buildComponentList(it.getChildByName("components")) }

        return itemComponentLists.map { it ->
            val item = Entity()
            it.forEach { comp -> item.add(comp) }
            item
        }
    }

    private fun buildComponentList(element: XmlReader.Element?) : List<Component> {
        val components = ArrayList<Component>()

        element?.let {
            for (i in 0..it.childCount - 1) {
                val entity = Entity()
                val child = element.getChild(i)

                buildComponentList(child).forEach { entity.add(it) }
                components.add(ComponentFactory.createComponent(compStringMap[child.name], child))
            }
        }

        return components
    }

    private fun buildEntityEq(element: XmlReader.Element?): HashMap<EqSlot, Entity> {
        val entityEq = HashMap<EqSlot, Entity>()

        element?.let {
            for (i in 0..it.childCount - 1) {
                val entity = Entity()

                buildComponentList(element.getChild(i)).forEach { entity.add(it) }
                entityEq.put(EqSlot.valueOf(element.getChild(i).name), entity)
            }
        }

        return entityEq
    }

}