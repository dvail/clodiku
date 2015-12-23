package com.dvail.clodiku

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.combat.WeaponClass
import com.dvail.clodiku.entities.*
import com.dvail.clodiku.file.DataLoader
import com.dvail.clodiku.file.DataSaver
import com.dvail.clodiku.world.GameEngine
import com.dvail.clodiku.world.Maps

// TODO Refactor this entire file out to a more appropriate place
val dataLoader = DataLoader()
val dataSaver = DataSaver()

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

private fun initPlayer(world: Engine) {
    val player = Entity()
    val weapon = Entity()
    val armor = Entity()

    player.add(Player("Inefray"))
    player.add(Spatial(910f, 860f, 14f, Direction.East))
    player.add(Attribute(50, 20, 50, 10, 10, 10, 10))
    player.add(State(BaseState.Walking))
    player.add(Equipment())
    player.add(Inventory())
    player.add(AnimatedRenderable("./player/"))

    weapon.add(Item("An emerald spear", "This spear doesn't look very sharp"))
    weapon.add(Spatial(Carried))
    weapon.add(Renderable("./items/emerald-spear.png"))
    weapon.add(EqItem(hr = 1, slot = EqSlot.Held))
    weapon.add(EqWeapon(WeaponClass.Spear, DamageType.Pierce, 5, 4f))

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

fun initMain(world: Engine){
    initPlayer(world)
    initMap(world, "sample")
    initArea(world, "sample")
}