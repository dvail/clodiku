package com.dvail.klodiku.entities

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.moandjiezana.toml.Toml
import java.io.File
import java.util.*

class DataLoader() {

    val compStringMap = HashMap<String, Class<out Component>>()

    init {
        compStringMap.put("spatial", Comps.Spatial)
        compStringMap.put("eqweapon", Comps.EqWeapon)
    }

    fun loadArea(world: Engine, areaName: String) {
        val areaData = File("./maps/$areaName/data.toml")
        val areaToml = Toml().parse(areaData)

        loadMobs(world, areaToml.getTables("mob"))
        loadFreeItems(world, areaToml.getTables("free-item"))
    }

    private fun loadMobs(world: Engine, mobs: List<Toml>) {

    }

    private fun loadFreeItems(world: Engine, items: List<Toml>) {
        val itemComponentMaps = items.map { loadItem(world, it) }

        itemComponentMaps.forEach { it ->
            val item = Entity()
            it.values.forEach { comp -> item.add(comp) }
            world.addEntity(item)
        }
    }

    private fun loadItem(world: Engine, item: Toml) : HashMap<Class<out Component>, Component> {
        val templateName = item.getString("template")
        val components = loadTemplateComponents(TemplateType.Item, templateName)
        val tomlOverrides = item.getList<String>("comp_overrides")
        val tomlComps = item.getTable("components")

        tomlOverrides.forEach { it ->
            val clazz = compStringMap[it]
            val comp =  createComponent(clazz, tomlComps.getTable(it))
            if (clazz != null && comp != null) components.put(clazz, comp)
        }

        return components
    }

    /*
    * This is awful...
    */
    private fun createComponent(clazz: Class<out Component>?, toml: Toml) : Component? {
        when (clazz) {
            Comps.Spatial -> return Spatial(toml.getDouble("x").toFloat(), toml.getDouble("y").toFloat(), toml.getDouble("radius").toFloat())
            else -> { return null }
        }
    }

}

