package com.dvail.clodiku

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.entities.WorldMap
import com.dvail.clodiku.file.DataLoader
import com.dvail.clodiku.file.DataSaver
import com.dvail.clodiku.world.GameEngine
import com.dvail.clodiku.world.Maps
import java.io.File

// TODO Refactor this entire file out to a more appropriate place
val dataLoader = DataLoader()
val dataSaver = DataSaver()
val playerStartFile = File("./PLAYER_START.toml")

fun saveGame(world: Engine) {
    val saveLocation = (world as GameEngine).saveLocation

    dataSaver.saveGame(world, saveLocation)
}

fun initArea(world: Engine, mapName: String) {
    dataLoader.loadArea(world, mapName)
}

fun initMap(world: Engine, mapName: String) {
    val map = Maps.loadMap(mapName)
    val grid = Maps.loadMapGrid(map)

    val mapEntity = Entity()
    mapEntity.add(WorldMap(mapName, map, grid))
    world.addEntity(mapEntity)
}

fun initMain(world: Engine, newGame: Boolean){
    val saveLocation = (world as GameEngine).saveLocation

    if (newGame) {
        playerStartFile.copyTo(File("$saveLocation/PLAYER.toml"))
    }

    dataLoader.loadPlayer(world, saveLocation)

    initMap(world, "sample")
    initArea(world, "sample")
}