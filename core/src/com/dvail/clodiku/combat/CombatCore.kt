package com.dvail.clodiku.combat

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.dvail.clodiku.entities.*
import com.dvail.clodiku.util.Entities
import com.dvail.clodiku.util.currentAnimation

val attackingStates = arrayOf(BaseState.Melee_Bash, BaseState.Melee_Pierce, BaseState.Melee_Slash)

fun getAttackers(world: Engine): Iterable<Entity> {
    return Entities.withComps(world, Comps.State).filter { it ->
        attackingStates.contains(CompMapper.State.get(it).current)
    }
}

fun initAttack(entity: Entity) {
    val weaponEntity = CompMapper.Equipment.get(entity).items[EqSlot.Held]

    if (weaponEntity != null) {
        val compEqWeapon = CompMapper.EqWeapon.get(weaponEntity)
        setEntityAttackState(entity, compEqWeapon)
        setAttackStartPos(entity, compEqWeapon)
        compEqWeapon.hitSet.clear()
    } else {
        println("Implement h2h combat")
    }
}

fun advanceAttackState(delta: Float, entity: Entity) {
    val stateComp = CompMapper.State.get(entity)
    val animation = currentAnimation(entity)

    val attackDuration = if (animation != null) {
        animation.keyFrames.size * animation.frameDuration
    } else {
        4/12f
    }

    if (stateComp.time > attackDuration) {
        stateComp.time = 0f
        stateComp.current = BaseState.Standing
    } else {
        stateComp.time += delta
    }
}

fun aggravate(entities: Set<Entity>) = entities.forEach { CompMapper.MobAI.get(it).state = MobState.Aggro }

private fun setEntityAttackState(entity: Entity, compEqWeapon: EqWeapon) {
    val weaponClass = compEqWeapon.weaponClass
    val stateComp = CompMapper.State.get(entity)

    stateComp.current = when (weaponClass) {
        WeaponClass.Spear -> BaseState.Melee_Pierce
        WeaponClass.Sword -> BaseState.Melee_Slash
        WeaponClass.Mace -> BaseState.Melee_Bash
        else -> throw Exception()
    }

    stateComp.time = 0f;
}
