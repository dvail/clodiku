package com.dvail.clodiku.entities

import com.badlogic.ashley.core.Component
import com.dvail.clodiku.combat.WeaponClass
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
                val weaponClass = WeaponClass.valueOf(toml.getString("weaponClass"))
                val damType = getDefaultWeaponDamType(weaponClass)
                return EqWeapon(weaponClass = weaponClass, damType = damType,
                        baseDamage = toml.getLong("baseDamage").toInt(), size = toml.getDouble("size").toFloat())
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
            is Player -> {
                "Player = { name = '${component.name}'}"
            }
            is Renderable -> {
                "Renderable = { textureSource = '${component.textureSource}'}"
            }
            is AnimatedRenderable -> {
                "AnimatedRenderable = { animDir = '${component.animDir}'}"
            }
            is Spatial -> {
                if (component.pos.equals(Carried)) {
                    "Spatial = { pos = 'Carried'}"
                } else {
                    "Spatial = { x = ${component.pos.x}, y = ${component.pos.y}, " +
                            "radius = ${component.pos.radius}, direction ='${component.direction.name}'}"
                }
            }
            is State -> {
                "State = { baseState = '${component.current.name}' }"
            }
            is MobAI -> {
                "MobAI = { mobState = '${component.state}', thinkSpeed = ${component.thinkSpeed} }"
            }
            is Inventory -> {
                "Inventory = {}"
            }
            is Item -> {
                "Item = {name = '${component.name}', description = '${component.description}'}"
            }
            is Equipment -> {
                "Equipment = {}"
            }
            is EqItem -> {
                "EqItem = { slot = '${component.slot.name}', hr = ${component.hr}, dr = ${component.dr}, " +
                        "ed = ${component.ed}, ms = ${component.ms}, pd = ${component.pd}, " +
                        "saves = ${component.saves} }"
            }
            is EqWeapon -> {
                "EqWeapon = { weaponClass = '${component.weaponClass.name}', damType = '${component.damType.name}', " +
                        "baseDamage = ${component.baseDamage}, size = ${component.size} }"
            }
            is EqArmor -> {
                "EqArmor = { bulk = ${component.bulk} }"
            }
            is Attribute -> {
                "Attribute = { hp = ${component.hp}, mp = ${component.mp}, mv = ${component.mv}, " +
                        "str = ${component.str}, dex = ${component.dex}, vit = ${component.vit}, psy = ${component.psy}}"
            }
            else -> ""
        }

    }

}

