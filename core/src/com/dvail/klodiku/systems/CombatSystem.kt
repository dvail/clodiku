package com.dvail.klodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Circle
import com.dvail.klodiku.combat.aggravate
import com.dvail.klodiku.combat.calcAttackDamage
import com.dvail.klodiku.combat.getAttackers
import com.dvail.klodiku.combat.updateHitBox
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.events.MeleeHitEvent
import com.dvail.klodiku.events.EventQueue
import com.dvail.klodiku.events.EventType
import com.dvail.klodiku.util.entitiesWithComps
import com.dvail.klodiku.util.firstEntityWithComp
import com.dvail.klodiku.util.getEntityCollisions
import com.dvail.klodiku.util.hasComp

class CombatSystem(eventQ: EventQueue) : CoreSystem(eventQ) {

    lateinit var world: Engine
    lateinit var player: Entity
    var delta = 0f

    override fun addedToEngine(engine: Engine) {
        world = engine;
        player = firstEntityWithComp(world, Comps.Player)
    }

    override fun update(sysDelta: Float) {
        delta = sysDelta

        val attackers = getAttackers(world)

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

        processAttack(attacker, weaponComp, newHit)

        if (hasComp(attacker, Comps.Player)) {
            aggravate(newHit)
        }
    }

    private fun getDefenders(attacker: Entity): List<Entity> {
        val defenderType = if (hasComp(attacker, Comps.MobAI)) Comps.Player else Comps.MobAI
        return entitiesWithComps(engine, defenderType).filter { CompMapper.State.get(it).current != BaseState.Dead }
    }

    private fun processAttack(attacker: Entity, weaponComp: EqWeapon, hitSet: Set<Entity>) {
        hitSet.forEach { it ->
            val damage = calcAttackDamage(world, attacker, it)
            val location = CompMapper.Spatial.get(it).pos
            val event = MeleeHitEvent(attacker, it, Circle(location.x, location.y, location.radius), damage)

            damageEntity(it, damage)
            eventQ.addEvent(EventType.Combat, event)
        }
        weaponComp.hitSet.addAll(hitSet)
    }

    private fun damageEntity(entity: Entity, damage: Int) {
        val attributes = CompMapper.Attribute.get(entity)
        attributes.hp -= damage

        if (attributes.hp <= 0) {
            CompMapper.State.get(entity).current = BaseState.Dead
        }
    }
}