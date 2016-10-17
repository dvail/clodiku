package com.dvail.clodiku.entities

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.XmlReader
import com.dvail.clodiku.combat.WeaponClass
import com.dvail.clodiku.combat.getDefaultWeaponDamType

object ComponentFactory {

    fun createComponent(clazz: Class<out Component>?, element: XmlReader.Element): Component {
        return when (clazz) {
            Comps.Player -> {
                Player(name = element.getAttribute("name"))
            }
            Comps.Renderable -> {
                Renderable(element.getAttribute("textureSource"))
            }
            Comps.AnimatedRenderable -> {
                 AnimatedRenderable(element.getAttribute("animDir"))
            }
            Comps.Spatial -> {
                if (element.attributes.containsKey("pos") && element.getAttribute("pos") == "Carried") {
                    Spatial(Carried.copy())
                } else {
                    Spatial(element.getFloat("x"), element.getFloat("y"), element.getFloat("radius"))
                }
            }
            Comps.State -> {
                 State(BaseState.valueOf(element.getAttribute("current")))
            }
            Comps.MobAI -> {
                 MobAI(MobState.valueOf(element.getAttribute("state")))
            }
            Comps.Inventory -> {
                 Inventory()
            }
            Comps.Item -> {
                 Item(element.getAttribute("name"), element.getAttribute("description"))
            }
            Comps.Equipment -> {
                 Equipment()
            }
            Comps.EqItem -> {
                 EqItem(EqSlot.valueOf(element.getAttribute("slot")))
            }
            Comps.EqWeapon -> {
                val weaponClass = WeaponClass.valueOf(element.getAttribute("weaponClass"))
                val damType = getDefaultWeaponDamType(weaponClass)
                EqWeapon(weaponClass = weaponClass, damType = damType,
                        baseDamage = element.getInt("baseDamage"), size = element.getFloat("size"))
            }
            Comps.EqArmor -> {
                 EqArmor(element.getInt("bulk"))
            }
            Comps.Attribute -> {
                 Attribute()
            }
            Comps.Martial -> {
                Martial()
            }
            else -> throw Exception("Invalid component type read from XML file.")
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
                if (component.pos == Carried) {
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
            is Martial -> {
                "Martial = {}"
            }
            else -> ""
        }

    }

}

