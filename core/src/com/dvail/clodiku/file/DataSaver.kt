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
    private val pFileName = "PLAYER"

    fun initPlayerFile(saveLocation: String) {
        val playerStartFile = File("./PLAYER_START.xml")
        playerStartFile.copyTo(File("$saveLocation/$pFileName.xml"))
    }

    fun saveGame(world: GameEngine, saveLocation: String) {
        val worldMap = Entities.firstWithComp(world, Comps.WorldMap)
        val currentArea = CompMapper.WorldMap.get(worldMap).mapName

        saveWorld(world, saveLocation, currentArea)
        savePlayer(world, saveLocation, currentArea)
    }

    fun saveWorld(world: GameEngine, saveLocation: String, currentArea: String) {
        val writer = StringWriter()
        val xml = XmlWriter(writer)
        val areaFile = SaveFile(saveLocation, currentArea)

        xml.element("area")
        saveArea(xml, world)
        xml.pop()

        areaFile.saveWith(writer)
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

        val pFile = SaveFile(saveLocation, pFileName)

        saveGameTime(xml, world)
        xml.element("area").text(currentArea).pop()

        xml.pop()
        pFile.saveWith(writer)
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

    private class SaveFile(val saveLocation: String, val fileName: String) {
        val tmpFile: File

        init {
            tmpFile = File("$saveLocation/$fileName.xml.tmp")

            if (tmpFile.exists()) tmpFile.delete()
            tmpFile.createNewFile()
        }

        fun saveWith(writer: StringWriter) {
            tmpFile.appendText(writer.toString())
            tmpFile.renameTo(File("$saveLocation/$fileName.xml"))
        }
    }

}