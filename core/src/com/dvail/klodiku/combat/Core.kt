package com.dvail.klodiku.combat

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.util.compData

fun initAttack(world: Engine, delta: Float, entity: Entity) {
    val weaponEntity = (compData(entity, CompMapper.Equipment) as Equipment).items[EqSlot.Held] as Entity
    val compEqWeapon = compData(weaponEntity, CompMapper.EqWeapon) as EqWeapon

    updateEntityState(entity, compEqWeapon)
    setAttackStartPos(entity, compEqWeapon)
}

fun advanceAttackState(world: Engine, delta: Float, entity: Entity) {

}

private fun updateEntityState(entity: Entity, compEqWeapon: EqWeapon) {
    val damType = compEqWeapon.damType
    val stateComp = (compData(entity, CompMapper.State) as State)

    stateComp.current = when (damType) {
        DamageType.Pierce -> BaseState.Melee_Pierce
        DamageType.Slash -> BaseState.Melee_Slash
        DamageType.Bash -> BaseState.Melee_Bash
        else -> throw Exception()
    }

    stateComp.time = 0f;
}
