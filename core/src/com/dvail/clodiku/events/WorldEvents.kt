package com.dvail.clodiku.events

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.maps.MapObject
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.util.Entities
import com.dvail.clodiku.world.GameEngine
import com.dvail.clodiku.world.Maps

data class SwapAreaEvent(val transportZone: MapObject) : Event {
    var worldUpdated = false
    var rendererUpdated = false

    override fun processEvent(world: Engine, delta: Float): Boolean {
        world as GameEngine

        if (!worldUpdated) {

            world.saveGame()

            val newArea = transportZone.properties.get("area-name") as String
            val tileX = transportZone.properties.get("tile-x").toString().toInt()
            val tileY = transportZone.properties.get("tile-y").toString().toInt()
            val player = Entities.firstWithComp(world, Comps.Player)

            val playerPos = CompMapper.Spatial.get(player).pos
            val newVector = Maps.tileToPixel(world, tileX, tileY)

            playerPos.x = newVector.x
            playerPos.y = newVector.y

            Entities.destroyNonPlayer(world)
            world.loadMap(newArea)
            world.loadArea(newArea)

            worldUpdated = true
        }

        return rendererUpdated
    }
}
