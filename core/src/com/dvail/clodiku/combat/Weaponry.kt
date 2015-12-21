package com.dvail.clodiku.combat

import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.DamageType
import com.dvail.clodiku.entities.Direction
import com.dvail.clodiku.entities.EqWeapon

object WeaponRange {
    val Sword = 28
}

enum class WeaponClass {
    Sword, Spear, Mace
}

fun getDefaultWeaponDamType(weaponClass: WeaponClass) : DamageType {
    return when (weaponClass) {
        WeaponClass.Sword -> DamageType.Slash
        WeaponClass.Spear -> DamageType.Pierce
        WeaponClass.Mace -> DamageType.Bash
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
            val startRange = (attackerSpatial.pos.radius / 2) + WeaponRange.Sword
            val offset = (startRange + compEqWeapon.hitBox.radius)

            compEqWeapon.hitBox.x = attackerSpatial.pos.x
            compEqWeapon.hitBox.y = attackerSpatial.pos.y

            when (attackerSpatial.direction) {
                Direction.West -> { compEqWeapon.hitBox.y += offset }
                Direction.East -> { compEqWeapon.hitBox.y -= offset }
                Direction.North -> { compEqWeapon.hitBox.x += offset }
                Direction.South -> { compEqWeapon.hitBox.x -= offset }
                else -> {}
            }
        }
        DamageType.Bash -> {

        }
        DamageType.Null -> {}
    }
}

fun updateHitBox(attacker: Entity, weaponComponent: EqWeapon) {
    val weaponType = weaponComponent.damType
    val spatial = CompMapper.Spatial.get(attacker)
    val direction = spatial.direction

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
            val moveRate = 8f
            val angle = Math.atan2((spatial.pos.x - weaponComponent.hitBox.x).toDouble(),
                    (spatial.pos.y - weaponComponent.hitBox.y).toDouble())

            weaponComponent.hitBox.x += (moveRate * Math.cos(angle).toFloat())
            weaponComponent.hitBox.y -= (moveRate * Math.sin(angle).toFloat())
        }
        DamageType.Bash -> {

        }
        DamageType.Null -> {}
    }
}
