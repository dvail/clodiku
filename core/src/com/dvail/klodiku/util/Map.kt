package com.dvail.klodiku.util

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector3
import com.dvail.klodiku.entities.CompMapper
import com.dvail.klodiku.entities.Comps
import com.dvail.klodiku.entities.Spatial
import com.dvail.klodiku.entities.WorldMap

private val mapLoader = TmxMapLoader()

class NoMapException : Exception()

fun loadMap(mapName: String): TiledMap = mapLoader.load("./maps/$mapName/map.tmx")

fun loadMapGrid(map: TiledMap): Array<IntArray> {
    val baseLayer = map.layers.get(0) as TiledMapTileLayer
    val width = baseLayer.width
    val height = baseLayer.height

    var grid = Array(width,  { IntArray(height) })

    for (rowNum in 0..width - 1) {
        for (colNum in 0..height - 1) {
            val walkable = (baseLayer.getCell(rowNum, colNum).tile.properties.get("walkable") as String?)
            grid[rowNum][colNum] = if (walkable != null && walkable == "false") -1 else 1
        }
    }

    return grid
}

fun currentMap(world: Engine): TiledMap {
    val entity = firstEntityWithComp(world, Comps.WorldMap)

    if (entity !== null) {
       val worldMap = compData(entity, CompMapper.WorldMap) as WorldMap
        return worldMap.tileMap
    } else {
        throw NoMapException()
    }
}

fun getMapBounds(world: Engine, camera: OrthographicCamera): Vector3 {
    val player = firstEntityWithComp(world, Comps.Player)
    val playerSpatial = compData(player as Entity, CompMapper.Spatial) as Spatial
    val playerPos = playerSpatial.pos
    val camWidth = camera.viewportWidth / 2
    val camHeight = camera.viewportHeight / 2
    val mapProps = currentMap(world).properties
    val mapWidth = mapProps.get("width") as Int * mapProps.get("tilewidth") as Int
    val mapHeight = mapProps.get("height") as Int * mapProps.get("tileheight") as Int

    val posX = if ((playerPos.x - camWidth) <= 0) {
        camWidth
    } else if ((playerPos.x + camWidth) >= mapWidth) {
        mapWidth - camWidth
    } else {
        playerPos.x
    }

    val posY = if ((playerPos.y - camHeight) <= 0) {
        camHeight
    } else if ((playerPos.y + camHeight) >= mapHeight) {
        mapHeight - camHeight
    } else {
        playerPos.y
    }

    return Vector3(posX, posY, 0f)
}