package com.dvail.clodiku.file

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.ComponentFactory
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.entities.EqSlot
import com.dvail.clodiku.util.Entities
import java.io.File
import java.util.*

class DataSaver {

    fun saveGame(world: Engine, saveLocation: String) {
        val worldMap = Entities.firstWithComp(world, Comps.WorldMap)
        val currentArea = CompMapper.WorldMap.get(worldMap).mapName

        saveArea(world, saveLocation, currentArea)
        savePlayer(world, saveLocation, currentArea)
    }

    fun saveArea(world: Engine, saveLocation: String, currentArea: String) {
        val areaFileTmp = File("$saveLocation/$currentArea.toml.tmp")

        if (areaFileTmp.exists()) areaFileTmp.delete()
        areaFileTmp.createNewFile()

        saveMobs(world, areaFileTmp)
        saveFreeItems(world, areaFileTmp)

        areaFileTmp.renameTo(File("$saveLocation/$currentArea.toml"))
    }

    private fun saveMobs(world: Engine, saveFile: File) {
        val mobs = Entities.withComps(world, Comps.MobAI)
        val mobTomls = mobs.map { getCharacterToml(it) }
        mobTomls.forEach { saveFile.appendText("[[mob]] \n$it") }
    }

    private fun saveFreeItems(world: Engine, saveFile: File) {
        val items = Entities.getFreeItems(world)
        val itemTomls = items.map { getEntityToml(it) }
        itemTomls.forEach { saveFile.appendText("[[free-item]]\ncomponents = {\n$it\n}\n") }
    }

    private fun savePlayer(world: Engine, saveLocation: String, currentArea: String) {
        val player = Entities.firstWithComp(world, Comps.Player)
        val playerToml = getCharacterToml(player)

        val playerFileTmp = File("$saveLocation/PLAYER.toml.tmp")

        if (playerFileTmp.exists()) playerFileTmp.delete()

        playerFileTmp.createNewFile()
        playerFileTmp.appendText("area = '''$currentArea'''\n")
        playerFileTmp.appendText(playerToml)
        playerFileTmp.renameTo(File("$saveLocation/PLAYER.toml"))
    }

    // This writes data for an entity assuming that entity have Inventory and Equipment
    // components.
    // TODO Abstract this out to handle arbitrary nested component interfaces if possible
    private fun getCharacterToml(entity: Entity) : String {
        val inventoryItems = CompMapper.Inventory.get(entity).items
        val equipmentItems = CompMapper.Equipment.get(entity).items

        val playerCompToml = getEntityToml(entity)
        val itemCompToml = getInventoryToml(inventoryItems)
        val eqCompToml = getEquipmentToml(equipmentItems)

        return "components = {\n$playerCompToml\n}\n inventory = [\n$itemCompToml\n]\n equipment = {\n$eqCompToml \n}\n"
    }

    private fun getEntityToml(entity: Entity): String {
        return entity.components.map { comp ->
            ComponentFactory.createToml(comp)
        }.joinToString(", \n")
    }

    private fun getInventoryToml(items: List<Entity>): String {
        val itemCompText = items.map { item ->
            item.components.map { comp ->
                ComponentFactory.createToml(comp)
            }.joinToString(", \n")
        }

        return if (itemCompText.size > 0) {
            "{ \n ${itemCompText.joinToString("\n}, \n { \n")} \n}"
        } else {
            ""
        }
    }

    private fun getEquipmentToml(eq: HashMap<EqSlot, Entity>): String {
        return eq.map { entry ->
            entry.key.name + " = {\n" + entry.value.components.map { comp ->
                ComponentFactory.createToml(comp)
            }.joinToString(", \n") + " \n} \n"
        }.joinToString(", \n")
    }

}