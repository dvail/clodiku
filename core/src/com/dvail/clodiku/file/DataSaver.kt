package com.dvail.clodiku.file

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.ComponentFactory
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.entities.EqSlot
import com.dvail.clodiku.util.firstEntityWithComp
import java.io.File
import java.util.*

class DataSaver {

    fun saveGame(world: Engine, saveLocation: String) {
        saveArea(world, saveLocation)
        savePlayer(world, saveLocation)
    }

    fun saveArea(world: Engine, saveLocation: String) {

    }

    private fun savePlayer(world: Engine, saveLocation: String) {
        val player = firstEntityWithComp(world, Comps.Player)
        val worldMap = firstEntityWithComp(world, Comps.WorldMap)

        val areaName = CompMapper.WorldMap.get(worldMap).mapName
        val inventoryItems = CompMapper.Inventory.get(player).items
        val equipmentItems = CompMapper.Equipment.get(player).items

        val playerCompToml = getEntityToml(player)
        val itemCompToml = getInventoryToml(inventoryItems)
        val eqCompToml = getEquipmentToml(equipmentItems)

        val playerToml = """
            area = '$areaName'
            components = {
               $playerCompToml
            }
            inventory = [
                $itemCompToml
            ]
            equipment = {
              $eqCompToml
            }
            """.trimIndent()

        val playerFileTmp = File(saveLocation + "/PLAYER.toml.tmp")

        if (playerFileTmp.exists()) playerFileTmp.delete()

        playerFileTmp.createNewFile()
        playerFileTmp.appendText(playerToml)
        playerFileTmp.renameTo(File(saveLocation + "/PLAYER.toml"))
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
            "{ \n ${itemCompText.joinToString("\n}, \n { \n")} }"
        } else {
            ""
        }
    }

    private fun getEquipmentToml(eq: HashMap<EqSlot, Entity>): String {
        return eq.map { entry ->
            entry.key.name + " = {\n" + entry.value.components.map { comp ->
                ComponentFactory.createToml(comp)
            }.joinToString(", \n") + " } \n"
        }.joinToString(", \n")
    }

}