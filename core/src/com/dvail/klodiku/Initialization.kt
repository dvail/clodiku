package com.dvail.klodiku

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.util.compData
import com.dvail.klodiku.util.firstEntityWithComp
import com.dvail.klodiku.util.loadMap
import com.dvail.klodiku.util.loadMapGrid
import java.util.*

private fun initMap(world: Engine, mapName: String) {
    val map = loadMap(mapName)
    val grid = loadMapGrid(map)

    var mapEntity = firstEntityWithComp(world, Comps.WorldMap)

    if (mapEntity != null) {
        var worldMap = compData(mapEntity, CompMapper.WorldMap) as WorldMap
        worldMap.tileMap = map
        worldMap.grid = grid
    } else {
        mapEntity = Entity()
        mapEntity.add(WorldMap(map, grid))
        world.addEntity(mapEntity)
    }

    initArea(world, mapName)
}

private fun initPlayer(world: Engine) {
    val player = Entity()
    val weapon = Entity()
    val armor = Entity()

    weapon.add(Item("An emerald spear", "This spear doesn't look very sharp"))
    weapon.add(Spatial(Carried, 16, Direction.None))
    weapon.add(Renderable("./items/emerald-spear.png"))
    weapon.add(EqItem(hr = 1, slot = EqSlot.Held))
    weapon.add(EqWeapon(5, Circle(0f, 0f, 4f), ArrayList<Entity>(0)))

    armor.add(Item("silver armor", "This armor is made of silver"))
    armor.add(Spatial(Carried, 16, Direction.None))
    armor.add(Renderable("./items/silver-scale-mail.png"))
    armor.add(EqItem(ed = 3, slot = EqSlot.Body))
    armor.add(EqArmor(bulk = 2))

    val playerEq = HashMap<EqSlot, Entity>()
    val playerEqStats = HashMap<Stat, Int>()
    val playerInventory = ArrayList<Entity>()

    player.add(Player("Inefray"))
    player.add(Spatial(Vector2(200f, 150f), 14, Direction.East))
    player.add(Attribute(50, 20, 50, 10, 10, 10, 10))
    player.add(State(PlayerState.Walking, 0f))
    player.add(Equipment(playerEq, playerEqStats))
    player.add(Inventory(playerInventory))

    // TODO Need to replace this with actual animation system
    player.add(Renderable("./player/melee-north-0.png"))

    world.addEntity(player)
    world.addEntity(weapon)
    world.addEntity(armor)

    playerEq.put(EqSlot.Held, weapon)
    playerInventory.add(armor)
}

private fun initArea(world: Engine, mapName: String) {
}

fun initMain(world: Engine){
    initPlayer(world)
    initMap(world, "sample")
}