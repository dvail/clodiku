package com.dvail.clodiku.file

import com.badlogic.ashley.core.Engine
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.ComponentFactory
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.util.firstEntityWithComp
import java.io.File

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
        val playerFileTmp = File(saveLocation + "/PLAYER.toml.tmp")
        val areaName = CompMapper.WorldMap.get(worldMap).mapName

        if (playerFileTmp.exists()) playerFileTmp.delete()

        playerFileTmp.createNewFile()

        // TODO There is probably a more efficient way of doing this

        playerFileTmp.appendText("area = '$areaName' \n")

        playerFileTmp.appendText("components = {\n")

        val compText = player.components.map { comp ->
            ComponentFactory.createToml(comp)
        }.joinToString(", \n")

        playerFileTmp.appendText(compText)
        playerFileTmp.appendText("}\n")

        playerFileTmp.appendText("inventory = [\n")
        val inventoryItems = CompMapper.Inventory.get(player).items

        val itemCompText = inventoryItems.map { item ->
            item.components.map { comp ->
                ComponentFactory.createToml(comp)
            }.joinToString(", \n")
        }

        if (itemCompText.size > 0) {
            playerFileTmp.appendText("{ \n")
            playerFileTmp.appendText(itemCompText.joinToString("\n}, \n { \n"))
            playerFileTmp.appendText(("}"))
        }

        playerFileTmp.appendText("]\n")
        playerFileTmp.appendText("equipment = { \n")

        val equipmentItems = CompMapper.Equipment.get(player).items

        val eqCompText = equipmentItems.map { entry ->
            entry.key.name + " = {\n" + entry.value.components.map { comp ->
                ComponentFactory.createToml(comp)
            }.joinToString(", \n") + " } \n"
        }.joinToString(", \n")

        playerFileTmp.appendText(eqCompText)
        playerFileTmp.appendText("\n }\n")

        playerFileTmp.renameTo(File(saveLocation + "/PLAYER.toml"))
    }

}