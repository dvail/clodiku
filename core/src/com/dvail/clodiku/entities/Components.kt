package com.dvail.clodiku.entities

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Circle
import com.dvail.clodiku.combat.WeaponClass
import com.dvail.clodiku.pathfinding.AStar
import com.dvail.clodiku.util.makeRegions
import com.dvail.clodiku.util.makeTexture
import java.util.*

enum class Direction { North, West, South, East, None }
enum class BaseState {
    Standing, Walking, Dead,
    Melee_H2H, Melee_Pierce, Melee_Slash, Melee_Bash
}

enum class MobState { Wander, Aggro }
enum class Stat { HP, MP, STR, DEX, VIT, PSY, HR, DR, MS, ED, PD, SAVES }

enum class EqSlot { Held, Head, Body, Arms, Legs, Feet, Hands }
enum class DamageType { Slash, Pierce, Bash, Null }

val Carried = Circle(-999f, -999f, 16f)

fun Circle.copy(x: Float = this.x, y: Float = this.y, radius: Float = this.radius): Circle {
    return Circle(x, y, radius)
}

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
    val Martial = Martial::class.java
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
    val Martial = ComponentMapper.getFor(Comps.Martial)
}

interface NestedComponent {
    fun getNestedEntities(): Collection<Entity>;
}

interface DisposableComponent {
    fun dispose();
}

data class Player(var name: String) : Component

data class WorldMap(var mapName: String, var tileMap: TiledMap, var grid: Array<IntArray>) : Component

data class Spatial(var pos: Circle, var direction: Direction) : Component {
    constructor(pos: Circle) : this(pos, Direction.None) {
    }

    constructor(x: Float, y: Float, radius: Float) : this(Circle(x, y, radius), Direction.None) {
    }

    constructor(x: Float, y: Float, radius: Float, direction: Direction) : this(Circle(x, y, radius), direction) {
    }
}

data class Renderable(var textureSource: String) : Component, DisposableComponent {
    override fun dispose() = texture.dispose()

    var texture = makeTexture(textureSource)
}

data class AnimatedRenderable(var animDir: String) : Component, DisposableComponent {
    override fun dispose() = atlas.dispose()

    var res = makeRegions(animDir)
    var animations = res.component1()
    var atlas = res.component2()
}

data class State(var current: BaseState) : Component {
    var time = 0f
}

data class Attribute(var hp: Int = 20, var mp: Int = 20, var mv: Int = 50, var str: Int = 10,
                     var dex: Int = 10, var vit: Int = 10, var psy: Int = 10) : Component

// This component is for entities who can fight. Right now this is mostly a shim to handle h2h combat without
// an ugly hack to the Equipment component
class Martial() : Component {
    var h2h: Entity? = null
}

// this component holds the total of all eq item stats for quick calculations
class Equipment() : Component, NestedComponent {
    var items = HashMap<EqSlot, Entity>()
    var statTotal = HashMap<Stat, Int>()

    override fun getNestedEntities(): Collection<Entity> = items.values
}

// A component for entities that can have stuff!
class Inventory() : Component, NestedComponent {
    var items = ArrayList<Entity>()

    override fun getNestedEntities(): Collection<Entity> = items
}

// A component for all basic item types
data class Item(var name: String, var description: String) : Component

data class EqItem(var slot: EqSlot, var hr: Int = 0, var dr: Int = 0, var ed: Int = 0,
                  var ms: Int = 0, var pd: Int = 0, var saves: Int = 0) : Component

// A weapon component has a hit box that checks for collisions, as well as a function that describes the motion of
// an attack
data class EqWeapon(var weaponClass: WeaponClass, var damType: DamageType, var baseDamage: Int, var size: Float) : Component {
    var hitBox = Circle(0f, 0f, size)
    var hitSet = HashSet<Entity>()
}

data class EqArmor(var bulk: Int) : Component

data class MobAI(var state: MobState, var thinkSpeed: Float) : Component {
    var lastUpdate = 0f
    var path = linkedListOf<AStar.Node>()

    constructor(state: MobState) : this(state, 3f) {
    }
}