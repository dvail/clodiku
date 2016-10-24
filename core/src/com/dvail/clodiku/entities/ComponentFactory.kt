package com.dvail.clodiku.entities

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.XmlReader
import com.badlogic.gdx.utils.XmlWriter
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
                Attribute(element.getIntAttribute("hp"),
                        element.getIntAttribute("maxHp"),
                        element.getIntAttribute("mp"),
                        element.getIntAttribute("maxMp"),
                        element.getIntAttribute("mv"),
                        element.getIntAttribute("maxMv"),
                        element.getIntAttribute("str"),
                        element.getIntAttribute("dex"),
                        element.getIntAttribute("vit"),
                        element.getIntAttribute("psy")
                )
            }
            Comps.Martial -> {
                Martial()
            }
            else -> throw Exception("Invalid component type read from XML file.")
        }
    }

    fun createXML(xml: XmlWriter, components: ImmutableArray<Component>) {
        components.forEach { createXML(xml, it) }
    }

    fun createXML(xml: XmlWriter, component: Component) {
        when (component) {
            is Player -> {
                xml.element("Player").attribute("name", component.name).pop()
            }
            is Renderable -> {
                xml.element("Renderable").attribute("textureSource", component.textureSource).pop()
            }
            is AnimatedRenderable -> {
                xml.element("AnimatedRenderable").attribute("animDir", component.animDir).pop()
            }
            is Spatial -> {
                xml.element("Spatial")

                if (component.pos == Carried) {
                    xml.attribute("pos", "Carried")
                } else {
                    xml.attribute("x", component.pos.x)
                            .attribute("y", component.pos.y)
                            .attribute("radius", component.pos.radius)
                            .attribute("direction", component.direction.name)
                }

                xml.pop()
            }
            is State -> {
                xml.element("State").attribute("current", component.current.name).pop()
            }
            is MobAI -> {
                xml.element("MobAI")
                        .attribute("state", component.state)
                        .attribute("thinkSpeed", component.thinkSpeed)
                        .pop()
            }
            is Inventory -> {
                xml.element("Inventory").pop()
            }
            is Item -> {
                xml.element("Item")
                        .attribute("name", component.name)
                        .attribute("description", component.description)
                        .pop()
            }
            is Equipment -> {
                xml.element("Equipment").pop()
            }
            is EqItem -> {
                xml.element("EqItem")
                        .attribute("slot", component.slot.name)
                        .attribute("hr", component.hr)
                        .attribute("dr", component.dr)
                        .attribute("ed", component.ed)
                        .attribute("ms", component.ms)
                        .attribute("pd", component.pd)
                        .attribute("saves", component.saves)
                        .pop()
            }
            is EqWeapon -> {
                xml.element("EqWeapon")
                        .attribute("weaponClass", component.weaponClass.name)
                        .attribute("damType", component.damType.name)
                        .attribute("baseDamage", component.baseDamage)
                        .attribute("size", component.size)
                        .pop()
            }
            is EqArmor -> {
                xml.element("EqArmor").attribute("bulk", component.bulk).pop()
            }
            is Attribute -> {
                xml.element("Attribute")
                        .attribute("hp", component.hp)
                        .attribute("maxHp", component.maxHp)
                        .attribute("mp", component.mp)
                        .attribute("maxMp", component.maxMp)
                        .attribute("mv", component.mv)
                        .attribute("maxMv", component.maxMv)
                        .attribute("str", component.str)
                        .attribute("dex", component.dex)
                        .attribute("vit", component.vit)
                        .attribute("psy", component.psy)
                        .pop()
            }
            is Martial -> {
                xml.element("Martial").pop()
            }
            else -> Unit
        }
    }

}

