package com.dvail.klodiku.combat

import com.badlogic.ashley.core.Entity
import com.dvail.klodiku.entities.*

fun setAttackStartPos(attackingEntity: Entity, compEqWeapon: EqWeapon) {
    val attackerSpatial = CompMapper.Spatial.get(attackingEntity)

    when (compEqWeapon.damType) {
        DamageType.Pierce -> {
            val startRange = attackerSpatial.pos.radius / 2
            val offset = (startRange + compEqWeapon.hitBox.radius)

            compEqWeapon.hitBox.x = attackerSpatial.pos.x
            compEqWeapon.hitBox.y = attackerSpatial.pos.y

            when (attackerSpatial.direction) {
                Direction.West -> { compEqWeapon.hitBox.x -= offset }
                Direction.East -> { compEqWeapon.hitBox.x += offset }
                Direction.North -> { compEqWeapon.hitBox.y += offset }
                Direction.South -> { compEqWeapon.hitBox.y -= offset }
                else -> {}
            }
        }
        DamageType.Slash -> {

        }
        DamageType.Bash -> {

        }
    }
}
