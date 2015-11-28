package com.dvail.klodiku

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.klodiku.entities.CompMapper
import com.dvail.klodiku.entities.Comps
import com.dvail.klodiku.entities.WorldMap
import com.dvail.klodiku.util.entityComp
import com.dvail.klodiku.util.firstEntityWithComp
import com.dvail.klodiku.util.loadMap
import com.dvail.klodiku.util.loadMapGrid

private fun initMap(world: Engine, mapName: String) {
    val map = loadMap(mapName)
    val grid = loadMapGrid(map)

    var mapEntity = firstEntityWithComp(world, Comps.WorldMap)

    if (mapEntity != null) {
        var worldMap = entityComp(mapEntity, CompMapper.WorldMap) as WorldMap
        worldMap.tileMap = map
        worldMap.grid = grid
    } else {
        mapEntity = Entity()
        mapEntity.add(WorldMap(map, grid))
        world.addEntity(Entity())
    }

    initArea(world, mapName)
}

private fun initPlayer(world: Engine) {

}

private fun initArea(world: Engine, mapName: String) {
}

fun initMain(world: Engine){
    initPlayer(world)
    initMap(world, "sample")
}