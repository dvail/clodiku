package com.dvail.klodiku.util

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader

private val mapLoader = TmxMapLoader()

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