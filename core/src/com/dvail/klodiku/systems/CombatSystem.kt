package com.dvail.klodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.dvail.klodiku.combat.updateHitBox
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.util.entitiesWithComps
import com.dvail.klodiku.util.firstEntityWithComp
import com.dvail.klodiku.util.getEntityCollisions
import com.dvail.klodiku.util.hasComp
import java.util.*

class CombatSystem : EntitySystem() {

    val attackingStates = arrayOf(BaseState.Melee_Bash, BaseState.Melee_Pierce, BaseState.Melee_Slash)

    lateinit var world: Engine
    lateinit var player: Entity
    var delta = 0f

    override fun addedToEngine(engine: Engine) {
        world = engine;
        player = firstEntityWithComp(world, Comps.Player)
    }

    override fun update(sysDelta: Float) {
        delta = sysDelta

        val attackers = entitiesWithComps(world, Comps.State).filter { it ->
            attackingStates.contains(CompMapper.State.get(it).current)
        }

        for (attacker in attackers) {

            val weaponEnt = CompMapper.Equipment.get(attacker).items[EqSlot.Held]
            val weaponComp = CompMapper.EqWeapon.get(weaponEnt)

            updateHitBox(attacker, weaponComp)
            checkAttackCollisions(attacker, weaponComp)
        }
    }

    private fun checkAttackCollisions(attacker: Entity, weaponComp: EqWeapon) {
        val defenders = getDefenders(attacker)
        val hitEntities = getEntityCollisions(weaponComp.hitBox, defenders)
        val newHit = hitEntities.minus(weaponComp.hitSet)

        weaponComp.hitSet.addAll(newHit)

        // TODO Finish
    }

    private fun getDefenders(attacker: Entity): ImmutableArray<Entity> {
        val defenderType = if (hasComp(attacker, Comps.MobAI)) Comps.MobAI else Comps.Player
        return entitiesWithComps(engine, defenderType)
    }
}