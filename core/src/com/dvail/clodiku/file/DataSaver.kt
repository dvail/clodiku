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

        val playerComp = CompMapper.Player.get(player)
        val spatialComp = CompMapper.Spatial.get(player)

        if (playerFileTmp.exists()) {
            playerFileTmp.delete()
        }

        playerFileTmp.createNewFile()

        // TODO There is probably a more efficient way of doing this

        playerFileTmp.appendText("area = \"$areaName\"\n")

        playerFileTmp.appendText("components = {\n")
        player.components.forEach { comp ->
            playerFileTmp.appendText(ComponentFactory.createToml(comp))
        }
        playerFileTmp.appendText("}\n")

        playerFileTmp.appendText("inventory = [\n")
        // write inventory
        playerFileTmp.appendText("]\n")

        playerFileTmp.appendText("equipment = { ")
        // write equipment
        playerFileTmp.appendText("}\n")

        playerFileTmp.renameTo(File(saveLocation + "/PLAYER.toml"))
    }

}