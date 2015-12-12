package com.dvail.klodiku.entities

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.klodiku.combat.getDefaultWeaponDamType
import com.moandjiezana.toml.Toml
import java.io.File
import java.util.*

class DataLoader() {

    val compStringMap = HashMap<String, Class<out Component>>()

    init {
        compStringMap.put("Spatial", Comps.Spatial)
        compStringMap.put("EqWeapon", Comps.EqWeapon)
        compStringMap.put("Renderable", Comps.Renderable)
    }

    fun loadArea(world: Engine, areaName: String) {
        val areaData = File("./maps/$areaName/data.toml")
        val areaToml = Toml().read(areaData)

      //  loadMobs(world, areaToml.getTables("mob"))
        loadFreeItems(world, areaToml.getTables("free-item"))
    }

    private fun loadMobs(world: Engine, mobs: List<Toml>) {
    }

    private fun loadFreeItems(world: Engine, items: List<Toml>) {
        val itemComponentLists = items.map { loadComponents(it) }

        itemComponentLists.forEach { it ->
            val item = Entity()
            it.forEach { comp -> item.add(comp) }
            world.addEntity(item)
        }
    }

    private fun loadComponents(item: Toml) : List<Component> {
        val compTable = item.getTable("components")
        val components = ArrayList<Component>()

        compStringMap.keys.forEach { key ->
            if (compTable.containsTable(key)) {
                val comp = createComponent(compStringMap[key], compTable.getTable(key))
                if (comp != null) components.add(comp)
            }
        }

        return components
    }

    private fun loadMobEq(toml: Toml) : HashMap<EqSlot, List<Component>> {
        val mobEq = HashMap<EqSlot, List<Component>>()

        EqSlot.values().forEach { slot ->
            val comp = toml.getTable(slot.toString().toLowerCase())
        }

        return mobEq
    }

    /*
    * This is awful...
    */
    private fun createComponent(clazz: Class<out Component>?, toml: Toml) : Component? {
        when (clazz) {
            Comps.Spatial -> {
                if (toml.containsPrimitive("pos") && toml.getString("pos").equals("Carried")) {
                    return Spatial(Carried)
                } else {
                    return Spatial(toml.getDouble("x").toFloat(), toml.getDouble("y").toFloat(), toml.getDouble("radius").toFloat())
                }
            }
            Comps.EqWeapon -> {
                val weaponType = toml.getString("type")
                val damType = getDefaultWeaponDamType(weaponType)
                return EqWeapon(damType = damType, baseDamage = toml.getLong("baseDamage").toInt(), size = toml.getDouble("size").toFloat())
            }
            Comps.Renderable -> {
                return Renderable(toml.getString("textureSource"))
            }
            else -> { return null }
        }
    }
}

