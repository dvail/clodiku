package com.dvail.clodiku.events

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.maps.MapObject
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.util.destroyNonPlayerEntities
import com.dvail.clodiku.util.firstEntityWithComp
import com.dvail.clodiku.world.GameEngine
import com.dvail.clodiku.world.Maps

data class SwapAreaEvent(val transportZone: MapObject): Event {
    var worldUpdated = false
    var rendererUpdated = false

    override fun processEvent(world: Engine, delta: Float): Boolean {
        if (!worldUpdated) {
            val newArea = transportZone.properties.get("area-name") as String
            val tileX = transportZone.properties.get("tile-x").toString().toInt()
            val tileY = transportZone.properties.get("tile-y").toString().toInt()
            val player = firstEntityWithComp(world, Comps.Player)

            val playerPos = CompMapper.Spatial.get(player).pos
            val newVector = Maps.tileToPixel(world, tileX, tileY)

            playerPos.x = newVector.x
            playerPos.y = newVector.y

            if (world is GameEngine) {
                world.saveGame()
                destroyNonPlayerEntities(world)
                world.initMap(newArea)
                world.initArea(newArea)
            }

            worldUpdated = true
        }

        return rendererUpdated
    }
}
