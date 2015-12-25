package com.dvail.clodiku.world

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.entities.WorldMap
import com.dvail.clodiku.file.DataLoader
import com.dvail.clodiku.file.DataSaver

class GameEngine(gameSaveLoc: String, loader: DataLoader, saver: DataSaver) : Engine() {
    var saveLocation = gameSaveLoc
    var paused = false

    val dataLoader = loader
    val dataSaver = saver

    fun saveGame() {
        dataSaver.saveGame(this, saveLocation)
    }

    fun loadArea(mapName: String) {
        dataLoader.loadArea(this, saveLocation, mapName)
    }

    fun loadMap(mapName: String) {
        val map = Maps.loadMap(mapName)
        val grid = Maps.loadMapGrid(map)

        val mapEntity = Entity()
        mapEntity.add(WorldMap(mapName, map, grid))
        this.addEntity(mapEntity)
    }

    fun loadPlayer(saveLocation: String) {
        dataLoader.loadPlayer(this, saveLocation)
    }
}
