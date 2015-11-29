package com.dvail.klodiku.combat

import com.badlogic.ashley.core.Entity
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.util.compData

fun setAttackStartPos(attackingEntity: Entity, compEqWeapon: EqWeapon) {
    val attackerSpatial = (compData(attackingEntity, CompMapper.Spatial) as Spatial)

    when (compEqWeapon.damType) {
        DamageType.Pierce -> {
            val startRange = attackerSpatial.pos.radius / 2

            when (attackerSpatial.direction) {
                Direction.West -> {
                    compEqWeapon.hitBox.x = attackerSpatial.pos.x - startRange - compEqWeapon.hitBox.radius
                    compEqWeapon.hitBox.y = attackerSpatial.pos.y
                }
                Direction.East -> {
                    compEqWeapon.hitBox.x = attackerSpatial.pos.x + startRange + compEqWeapon.hitBox.radius
                    compEqWeapon.hitBox.y = attackerSpatial.pos.y
                }
                Direction.North -> {
                    compEqWeapon.hitBox.x = attackerSpatial.pos.x
                    compEqWeapon.hitBox.y = attackerSpatial.pos.y + startRange + compEqWeapon.hitBox.radius
                }
                Direction.South -> {
                    compEqWeapon.hitBox.x = attackerSpatial.pos.x
                    compEqWeapon.hitBox.y = attackerSpatial.pos.y - startRange - compEqWeapon.hitBox.radius
                }
                else -> {}
            }
        }
        DamageType.Slash -> {

        }
        DamageType.Bash -> {

        }
    }
}
