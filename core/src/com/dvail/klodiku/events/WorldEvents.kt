package com.dvail.klodiku.events

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.maps.MapObject
import com.dvail.klodiku.entities.CompMapper
import com.dvail.klodiku.entities.Comps
import com.dvail.klodiku.initArea
import com.dvail.klodiku.initMap
import com.dvail.klodiku.util.destroyNonPlayerEntities
import com.dvail.klodiku.util.firstEntityWithComp
import com.dvail.klodiku.world.Maps

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

            destroyNonPlayerEntities(world)
            initMap(world, newArea)
            initArea(world, newArea)

            worldUpdated = true
        }

        return rendererUpdated
    }
}
