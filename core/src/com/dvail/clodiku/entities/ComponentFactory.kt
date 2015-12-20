package com.dvail.clodiku.entities

import com.badlogic.ashley.core.Component
import com.dvail.clodiku.combat.getDefaultWeaponDamType
import com.moandjiezana.toml.Toml

object ComponentFactory {

    fun createComponent(clazz: Class<out Component>?, toml: Toml): Component {
        when (clazz) {
            Comps.Renderable -> {
                return Renderable(toml.getString("textureSource"))
            }
            Comps.AnimatedRenderable -> {
                return AnimatedRenderable(toml.getString("animDir"))
            }
            Comps.Spatial -> {
                if (toml.containsPrimitive("pos") && toml.getString("pos").equals("Carried")) {
                    return Spatial(Carried)
                } else {
                    return Spatial(toml.getDouble("x").toFloat(), toml.getDouble("y").toFloat(), toml.getDouble("radius").toFloat())
                }
            }
            Comps.State -> {
                return State(BaseState.valueOf(toml.getString("baseState")))
            }
            Comps.MobAI -> {
                return MobAI(MobState.valueOf(toml.getString("mobState")))
            }
            Comps.Inventory -> {
                return Inventory()
            }
            Comps.Item -> {
                return Item(toml.getString("name"), toml.getString("description"))
            }
            Comps.Equipment -> {
                return Equipment()
            }
            Comps.EqItem -> {
                return EqItem(EqSlot.valueOf(toml.getString("slot")))
            }
            Comps.EqWeapon -> {
                val weaponType = toml.getString("type")
                val damType = getDefaultWeaponDamType(weaponType)
                return EqWeapon(damType = damType, baseDamage = toml.getLong("baseDamage").toInt(), size = toml.getDouble("size").toFloat())
            }
            Comps.EqArmor -> {
                return EqArmor(toml.getLong("bulk").toInt())
            }
            Comps.Attribute -> {
                return Attribute()
            }
            else -> {
                throw Exception("Invalid component type read from TOML file.")
            }
        }
    }

    fun createToml(component: Component) : String {
        return when (component) {
            is Renderable -> {
                "Renderable = {textureSource = \"${component.textureSource}\"}\n"
            }
            is AnimatedRenderable -> {
                "AnimatedRenderable = {animDir = \"${component.animDir}\"}\n"
            }
            is Spatial -> {
                // Check Carried
                "Spatial = {x = ${component.pos.x}, y = ${component.pos.y}, " +
                        "radius = ${component.pos.radius}, direction =\"${component.direction.name}\"}\n"
            }
            /*
            is State -> {
            }
            is MobAI -> {
            }
            is Inventory -> {
            }
            is Item -> {
            }
            is Equipment -> {
            }
            is EqItem -> {
            }
            is EqWeapon -> {
            }
            is EqArmor -> {
            }
            is Attribute -> {
            }
            */
            else -> ""
        }

    }

}

