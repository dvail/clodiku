package com.dvail.clodiku.world

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.util.Entities

object Maps {

    private val mapLoader = TmxMapLoader()

    const val MAP_TILE_SIZE = 32f
    const val HALF_TILE_SIZE = MAP_TILE_SIZE / 2

    fun loadMap(mapName: String): TiledMap = mapLoader.load("./maps/$mapName/map.tmx")

    fun loadMapGrid(map: TiledMap): Array<IntArray> {
        val baseLayer = map.layers.get(0) as TiledMapTileLayer
        val width = baseLayer.width
        val height = baseLayer.height

        var grid = Array(width, { IntArray(height) })

        for (rowNum in 0..width - 1) {
            for (colNum in 0..height - 1) {
                val walkable = (baseLayer.getCell(rowNum, colNum).tile.properties.get("walkable") as String?)
                grid[rowNum][colNum] = if (walkable != null && walkable == "false") -1 else 1
            }
        }

        return grid
    }

    fun currentMap(world: Engine): TiledMap {
        val entity = Entities.firstWithComp(world, Comps.WorldMap)
        val worldMap = CompMapper.WorldMap.get(entity)

        return worldMap.tileMap
    }

    fun getMapBounds(world: Engine, camera: OrthographicCamera): Vector3 {
        val player = Entities.firstWithComp(world, Comps.Player)
        val playerSpatial = CompMapper.Spatial.get(player)
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

    fun mapObstacles(world: Engine): MapObjects = currentMap(world).layers.get("collision").objects

    fun getTransportZones(world: Engine) = currentMap(world).layers.get("transport").objects

    fun mapHeightInTiles(world: Engine) : Int = currentMap(world).properties.get("width").toString().toInt()

    fun tileToPixel(world: Engine, tileX : Int, tileY : Int) : Vector2 {
        val newX = ((tileX * MAP_TILE_SIZE) + HALF_TILE_SIZE).toFloat()
        val newY = (((mapHeightInTiles(world) - tileY) * MAP_TILE_SIZE) + HALF_TILE_SIZE).toFloat()
        return Vector2(newX, newY)
    }

}
