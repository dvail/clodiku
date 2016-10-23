package com.dvail.clodiku.file

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.XmlWriter
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.ComponentFactory
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.entities.EqSlot
import com.dvail.clodiku.util.Entities
import com.dvail.clodiku.world.GameEngine
import java.io.File
import java.io.StringWriter
import java.util.*

class DataSaver {

    fun saveGame(world: Engine, saveLocation: String) {
        world as GameEngine

        val worldMap = Entities.firstWithComp(world, Comps.WorldMap)
        val currentArea = CompMapper.WorldMap.get(worldMap).mapName

        val writer = StringWriter()
        val xml = XmlWriter(writer)

        xml.element("area")

        // TODO Clean up this file handling code between saveArea and savePlayer functions
        val areaFileTmp = File("$saveLocation/$currentArea.xml.tmp")

        if (areaFileTmp.exists()) areaFileTmp.delete()
        areaFileTmp.createNewFile()

        saveArea(xml, world)
        savePlayer(world, saveLocation, currentArea)

        xml.pop()
        areaFileTmp.appendText(writer.toString())
        areaFileTmp.renameTo(File("$saveLocation/$currentArea.xml"))
    }

    fun saveGameTime(xml: XmlWriter, world: GameEngine) {
        xml.element("last-save").text(world.gameTime).pop()
    }

    fun saveArea(xml: XmlWriter, world: GameEngine) {
        saveGameTime(xml, world)
        saveMobs(xml, world)
        saveFreeItems(xml, world)
    }

    private fun saveMobs(xml: XmlWriter, world: Engine) {
        val mobs = Entities.withComps(world, Comps.MobAI)
        mobs.forEach {
            xml.element("mob").buildCharacterXML(it).pop()
        }
    }

    private fun saveFreeItems(xml: XmlWriter, world: Engine) {
        val items = Entities.getFreeItems(world)
        items.forEach {
            xml.element("free-item").buildEntityXML(it).pop()
        }
    }

    private fun savePlayer(world: GameEngine, saveLocation: String, currentArea: String) {
        val writer = StringWriter()
        val xml = XmlWriter(writer)

        xml.element("player")

        val player = Entities.firstWithComp(world, Comps.Player)
        xml.buildCharacterXML(player)

        val playerFileTmp = File("$saveLocation/PLAYER.xml.tmp")
        if (playerFileTmp.exists()) playerFileTmp.delete()
        playerFileTmp.createNewFile()

        xml.element("last-save").text(world.gameTime).pop()
        xml.element("last-area").text(currentArea).pop()

        xml.pop()
        playerFileTmp.appendText(writer.toString())
        playerFileTmp.renameTo(File("$saveLocation/PLAYER.xml"))
    }

    // This writes data for an entity assuming that entity have Inventory and Equipment
    // components.
    // TODO Abstract this out to handle arbitrary nested component interfaces if possible
    private fun XmlWriter.buildCharacterXML(entity: Entity) : XmlWriter {
        val inventoryItems = CompMapper.Inventory.get(entity).items
        val equipmentItems = CompMapper.Equipment.get(entity).items

        return this.buildEntityXML(entity)
                .buildInventoryXML(inventoryItems)
                .buildEquipmentXML(equipmentItems)
    }

    private fun XmlWriter.buildEntityXML(entity: Entity) : XmlWriter {
        this.element("components")
        ComponentFactory.createXML(this, entity.components)
        this.pop()

        return this
    }

    private fun XmlWriter.buildInventoryXML(items: List<Entity>) : XmlWriter {
        this.element("inventory")
        items.forEach {
            this.element("item")
            ComponentFactory.createXML(this, it.components)
            this.pop()
        }
        this.pop()

        return this
    }

    private fun XmlWriter.buildEquipmentXML(eq: HashMap<EqSlot, Entity>) : XmlWriter {
        this.element("equipment")

        eq.forEach { eqSlot, entity ->
            this.element(eqSlot.name)
            ComponentFactory.createXML(this, entity.components)
            this.pop()
        }

        this.pop()

        return this
    }

}