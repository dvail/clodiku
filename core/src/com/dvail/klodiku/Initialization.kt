package com.dvail.klodiku

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.util.loadMap
import com.dvail.klodiku.util.loadMapGrid
import com.moandjiezana.toml.Toml
import java.io.File
import java.util.*

private fun initMap(world: Engine, mapName: String) {
    val map = loadMap(mapName)
    val grid = loadMapGrid(map)

    val mapEntity = Entity()
    mapEntity.add(WorldMap(map, grid))
    world.addEntity(mapEntity)

    initArea(world, mapName)
}

private fun initPlayer(world: Engine) {
    val player = Entity()
    val weapon = Entity()
    val armor = Entity()

    player.add(Player("Inefray"))
    player.add(Spatial(180f, 140f, 14f, Direction.East))
    player.add(Attribute(50, 20, 50, 10, 10, 10, 10))
    player.add(State(BaseState.Walking))
    player.add(Equipment())
    player.add(Inventory())
    player.add(AnimatedRenderable("./player/"))

    weapon.add(Item("An emerald spear", "This spear doesn't look very sharp"))
    weapon.add(Spatial(Carried))
    weapon.add(Renderable("./items/emerald-spear.png"))
    weapon.add(EqItem(hr = 1, slot = EqSlot.Held))
    weapon.add(EqWeapon(DamageType.Pierce, 5, 4f))

    armor.add(Item("silver armor", "This armor is made of silver"))
    armor.add(Spatial(Carried))
    armor.add(Renderable("./items/silver-scale-mail.png"))
    armor.add(EqItem(ed = 3, slot = EqSlot.Body))
    armor.add(EqArmor(bulk = 2))

    world.addEntity(player)
    world.addEntity(weapon)
    world.addEntity(armor)

    CompMapper.Equipment.get(player).items.put(EqSlot.Held, weapon)
    CompMapper.Inventory.get(player).items.add(armor)
}

private fun initArea(world: Engine, mapName: String) {
}

fun initMain(world: Engine){
    initPlayer(world)
    initMap(world, "sample")
}