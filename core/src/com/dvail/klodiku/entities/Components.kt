package com.dvail.klodiku.entities

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Circle
import com.dvail.klodiku.pathfinding.AStar
import com.dvail.klodiku.util.makeRegions
import com.dvail.klodiku.util.makeTexture
import java.util.*

enum class Direction { North, West, South, East, None }
enum class BaseState { Standing, Walking, Melee_Pierce, Melee_Slash, Melee_Bash, Dead }
enum class MobState { Wander, Aggro }
enum class Stat { HP, MP, STR, DEX, VIT, PSY, HR, DR, MS, ED, PD, SAVES }

enum class EqSlot { Held, Head, Body, Arms, Legs, Feet, Hands }
enum class DamageType { Slash, Pierce, Bash }

val Carried = Circle(-99f, -99f, 16f)

object Comps {
    val Player = Player::class.java
    val WorldMap = WorldMap::class.java
    val Spatial = Spatial::class.java
    val Renderable = Renderable::class.java
    val AnimatedRenderable = AnimatedRenderable::class.java
    val State = State::class.java
    val Attribute = Attribute::class.java
    val Equipment = Equipment::class.java
    val Inventory = Inventory::class.java
    val Item = Item::class.java
    val EqItem = EqItem::class.java
    val EqWeapon = EqWeapon::class.java
    val EqArmor = EqArmor::class.java
    val MobAI = MobAI::class.java
}

object CompMapper {
    val Player = ComponentMapper.getFor(Comps.Player)
    val WorldMap = ComponentMapper.getFor(Comps.WorldMap)
    val Spatial = ComponentMapper.getFor(Comps.Spatial)
    val Renderable = ComponentMapper.getFor(Comps.Renderable)
    val AnimatedRenderable = ComponentMapper.getFor(Comps.AnimatedRenderable)
    val State = ComponentMapper.getFor(Comps.State)
    val Attribute = ComponentMapper.getFor(Comps.Attribute)
    val Equipment = ComponentMapper.getFor(Comps.Equipment)
    val Inventory = ComponentMapper.getFor(Comps.Inventory)
    val Item = ComponentMapper.getFor(Comps.Item)
    val EqItem = ComponentMapper.getFor(Comps.EqItem)
    val EqWeapon = ComponentMapper.getFor(Comps.EqWeapon)
    val EqArmor = ComponentMapper.getFor(Comps.EqArmor)
    val MobAI = ComponentMapper.getFor(Comps.MobAI)
}

data class Player(var name: String) : Component

data class WorldMap(var tileMap: TiledMap, var grid: Array<IntArray>) : Component

data class Spatial(var pos: Circle, var direction: Direction) : Component

data class Renderable(var textureSource: String) : Component {
    var texture = makeTexture(textureSource)
}

data class AnimatedRenderable(var animDir: String) : Component {
    var animations = makeRegions(animDir)
}

data class State(var current: BaseState, var time: Float) : Component

data class Attribute(var hp: Int, var mp: Int, var mv: Int, var str: Int,
                     var dex: Int, var vit: Int, var psy: Int) : Component

// this component holds the total of all eq item stats for quick calculations
data class Equipment(var items: HashMap<EqSlot, Entity>, var statTotal: HashMap<Stat, Int>) : Component

// A component for entities that can have stuff!
data class Inventory(var items: ArrayList<Entity>) : Component

// A component for all basic item types
data class Item(var name: String, var description: String) : Component

data class EqItem(var slot: EqSlot, var hr: Int = 0, var dr: Int = 0, var ed: Int = 0,
                  var ms: Int = 0, var pd: Int = 0, var saves: Int = 0) : Component

// A weapon component has a hit box that checks for collisions, as well as a function that describes the motion of
// an attack
data class EqWeapon(var baseDamage: Int, var damType: DamageType, var hitBox: Circle, var hitSet: HashSet<Entity>) : Component

// TODO Need to better define properties of armor, and all eq for that matter
data class EqArmor(var bulk: Int) : Component

data class MobAI(var state: MobState, var lastUpdate: Float, var path: Array<AStar.Node>) : Component
