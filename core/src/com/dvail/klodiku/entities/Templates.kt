package com.dvail.klodiku.entities

import com.badlogic.ashley.core.Component
import java.util.*

enum class TemplateType {
    Item, Mob
}

fun loadTemplateComponents(type: TemplateType, name: String): HashMap<Class<out Component>, Component> {
    when (type) {
        TemplateType.Item -> return loadItemTemplate(name)
        TemplateType.Mob -> return loadMobTemplate(name)
    }
}

private fun loadItemTemplate(name: String): HashMap<Class<out Component>, Component> {
    var comps = HashMap<Class<out Component>, Component>()

    when (name) {
        "sword" -> {
            comps.put(Comps.Item, Item("A short sword", "This short sword is dull"))
            comps.put(Comps.Renderable, Renderable("./items/steel-sword.png"))
            comps.put(Comps.Spatial, Spatial(Carried))
            comps.put(Comps.EqItem, EqItem(hr = 1, slot = EqSlot.Held))
            comps.put(Comps.EqWeapon, EqWeapon(DamageType.Slash, 2, 4f))
        }
    }

    return comps
}

private fun loadMobTemplate(name: String): HashMap<Class<out Component>, Component> {
    var comps = HashMap<Class<out Component>, Component>()

    when (name) {
        "orc" -> {
            comps.put(Comps.State, State(BaseState.Standing))
            comps.put(Comps.AnimatedRenderable, AnimatedRenderable("./mob/orc/"))
            comps.put(Comps.Spatial, Spatial(0f, 0f, 14f, Direction.West))
            comps.put(Comps.Equipment, Equipment())
            comps.put(Comps.MobAI, MobAI(MobState.Wander))
        }
    }

    return comps
}