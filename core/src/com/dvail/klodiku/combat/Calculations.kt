package com.dvail.klodiku.combat

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.klodiku.entities.CompMapper
import com.dvail.klodiku.entities.EqSlot

// TODO Lots of work to be done here
fun calcAttackDamage(world: Engine, attacker: Entity, defender: Entity): Int {
    val attackerWeapon = CompMapper.Equipment.get(attacker).items[EqSlot.Held]
    return CompMapper.EqWeapon.get(attackerWeapon).baseDamage
}
