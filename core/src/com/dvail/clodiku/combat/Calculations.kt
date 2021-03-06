package com.dvail.clodiku.combat

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.Comps

// TODO Lots of work to be done here
fun calcAttackDamage(world: Engine, attacker: Entity, defender: Entity): Int {
    val attackerWeapon = Weaponry.getWeapon(attacker)
    return CompMapper.EqWeapon.get(attackerWeapon).baseDamage
}

fun regenEntity(entity: Entity) {
    val attrComp = entity.getComponent(Comps.Attribute)

    attrComp.hp = Math.min(attrComp.maxHp, attrComp.hp + (attrComp.vit / 10))
}
