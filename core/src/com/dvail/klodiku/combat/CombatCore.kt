package com.dvail.klodiku.combat

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.util.currentAnimation
import com.dvail.klodiku.util.entitiesWithComps

val attackingStates = arrayOf(BaseState.Melee_Bash, BaseState.Melee_Pierce, BaseState.Melee_Slash)

fun getAttackers(world: Engine): Iterable<Entity> {
    return entitiesWithComps(world, Comps.State).filter { it ->
        attackingStates.contains(CompMapper.State.get(it).current)
    }
}

fun initAttack(entity: Entity) {
    val weaponEntity = CompMapper.Equipment.get(entity).items[EqSlot.Held]
    val compEqWeapon = CompMapper.EqWeapon.get(weaponEntity)

    setEntityAttackState(entity, compEqWeapon)
    setAttackStartPos(entity, compEqWeapon)
}

fun advanceAttackState(delta: Float, entity: Entity) {
    val stateComp = CompMapper.State.get(entity)
    val animation = currentAnimation(entity)

    if (animation != null && stateComp.time > (animation.keyFrames.size * animation.frameDuration)) {
        stateComp.time = 0f
        stateComp.current = BaseState.Standing
    } else {
        stateComp.time += delta
    }
}

fun aggravate(entities: Set<Entity>) = entities.forEach { CompMapper.MobAI.get(it).state = MobState.Aggro }

private fun setEntityAttackState(entity: Entity, compEqWeapon: EqWeapon) {
    val damType = compEqWeapon.damType
    val stateComp = CompMapper.State.get(entity)

    stateComp.current = when (damType) {
        DamageType.Pierce -> BaseState.Melee_Pierce
        DamageType.Slash -> BaseState.Melee_Slash
        DamageType.Bash -> BaseState.Melee_Bash
        else -> throw Exception()
    }

    stateComp.time = 0f;
}