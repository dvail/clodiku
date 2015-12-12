package com.dvail.klodiku.combat

import com.badlogic.ashley.core.Entity
import com.dvail.klodiku.entities.*

fun getDefaultWeaponDamType(weaponType: String) : DamageType {
    return when (weaponType) {
        "sword" -> DamageType.Slash
        "spear" -> DamageType.Pierce
        "mace" -> DamageType.Bash
        else -> DamageType.Null
    }
}

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
        DamageType.Null -> {}
    }
}

fun updateHitBox(attacker: Entity, weaponComponent: EqWeapon) {
    val weaponType = weaponComponent.damType
    val direction = CompMapper.Spatial.get(attacker).direction

    when (weaponType) {
        DamageType.Pierce -> {
            val moveRate = 2

            when (direction) {
                Direction.West -> { weaponComponent.hitBox.x -= moveRate }
                Direction.East -> { weaponComponent.hitBox.x += moveRate }
                Direction.North -> { weaponComponent.hitBox.y += moveRate }
                Direction.South -> { weaponComponent.hitBox.y -= moveRate }
                else -> {}
            }
        }
        DamageType.Slash -> {

        }
        DamageType.Bash -> {

        }
    }
}
