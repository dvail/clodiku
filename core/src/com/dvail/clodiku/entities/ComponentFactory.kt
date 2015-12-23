package com.dvail.clodiku.entities

import com.badlogic.ashley.core.Component
import com.dvail.clodiku.combat.WeaponClass
import com.dvail.clodiku.combat.getDefaultWeaponDamType
import com.moandjiezana.toml.Toml

object ComponentFactory {

    fun createComponent(clazz: Class<out Component>?, toml: Toml): Component {
        return when (clazz) {
            Comps.Renderable -> {
                Renderable(toml.getString("textureSource"))
            }
            Comps.AnimatedRenderable -> {
                 AnimatedRenderable(toml.getString("animDir"))
            }
            Comps.Spatial -> {
                if (toml.containsPrimitive("pos") && toml.getString("pos").equals("Carried")) {
                     Spatial(Carried)
                } else {
                     Spatial(toml.getDouble("x").toFloat(), toml.getDouble("y").toFloat(), toml.getDouble("radius").toFloat())
                }
            }
            Comps.State -> {
                 State(BaseState.valueOf(toml.getString("current")))
            }
            Comps.MobAI -> {
                 MobAI(MobState.valueOf(toml.getString("state")))
            }
            Comps.Inventory -> {
                 Inventory()
            }
            Comps.Item -> {
                 Item(toml.getString("name"), toml.getString("description"))
            }
            Comps.Equipment -> {
                 Equipment()
            }
            Comps.EqItem -> {
                 EqItem(EqSlot.valueOf(toml.getString("slot")))
            }
            Comps.EqWeapon -> {
                val weaponClass = WeaponClass.valueOf(toml.getString("weaponClass"))
                val damType = getDefaultWeaponDamType(weaponClass)
                 EqWeapon(weaponClass = weaponClass, damType = damType,
                        baseDamage = toml.getLong("baseDamage").toInt(), size = toml.getDouble("size").toFloat())
            }
            Comps.EqArmor -> {
                 EqArmor(toml.getLong("bulk").toInt())
            }
            Comps.Attribute -> {
                 Attribute()
            }
            else -> throw Exception("Invalid component type read from TOML file.")
        }
    }

    fun createToml(component: Component) : String {
        return when (component) {
            is Player -> {
                "Player = { name = '''${component.name}'''}"
            }
            is Renderable -> {
                "Renderable = { textureSource = '''${component.textureSource}'''}"
            }
            is AnimatedRenderable -> {
                "AnimatedRenderable = { animDir = '''${component.animDir}'''}"
            }
            is Spatial -> {
                if (component.pos.equals(Carried)) {
                    "Spatial = { pos = '''Carried'''}"
                } else {
                    "Spatial = { x = ${component.pos.x}, y = ${component.pos.y}, " +
                            "radius = ${component.pos.radius}, direction = '''${component.direction.name}'''}"
                }
            }
            is State -> {
                "State = { current = '''${component.current.name}''' }"
            }
            is MobAI -> {
                "MobAI = { state = '''${component.state}''', thinkSpeed = ${component.thinkSpeed} }"
            }
            is Inventory -> {
                "Inventory = {}"
            }
            is Item -> {
                "Item = {name = '''${component.name}''', description = '''${component.description}'''}"
            }
            is Equipment -> {
                "Equipment = {}"
            }
            is EqItem -> {
                "EqItem = { slot = '''${component.slot.name}''', hr = ${component.hr}, dr = ${component.dr}, " +
                        "ed = ${component.ed}, ms = ${component.ms}, pd = ${component.pd}, " +
                        "saves = ${component.saves} }"
            }
            is EqWeapon -> {
                "EqWeapon = { weaponClass = '''${component.weaponClass.name}''', damType = '''${component.damType.name}''', " +
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

