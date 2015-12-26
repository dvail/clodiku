package com.dvail.clodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Circle
import com.dvail.clodiku.combat.aggravate
import com.dvail.clodiku.combat.calcAttackDamage
import com.dvail.clodiku.combat.getAttackers
import com.dvail.clodiku.combat.updateHitBox
import com.dvail.clodiku.entities.*
import com.dvail.clodiku.events.EventQueue
import com.dvail.clodiku.events.EventType
import com.dvail.clodiku.events.MeleeHitEvent
import com.dvail.clodiku.util.Entities
import com.dvail.clodiku.util.Movement
import com.dvail.clodiku.util.hasComp
import com.dvail.clodiku.world.GameEngine

class CombatSystem(eventQ: EventQueue) : CoreSystem(eventQ) {

    lateinit var world: GameEngine
    lateinit var player: Entity
    var delta = 0f

    override fun addedToEngine(engine: Engine) {
        world = engine as GameEngine
        player = Entities.firstWithComp(world, Comps.Player)
    }

    override fun update(sysDelta: Float) {
        if (world.paused) return

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
        val hitEntities = Movement.getEntityCollisions(weaponComp.hitBox, defenders)
        val newHit = hitEntities.minus(weaponComp.hitSet)

        weaponComp.hitSet.addAll(newHit)

        processAttack(attacker, weaponComp, newHit)

        if (attacker.hasComp(Comps.Player)) {
            aggravate(newHit)
        }
    }

    private fun getDefenders(attacker: Entity): List<Entity> {
        val defenderType = if (attacker.hasComp(Comps.MobAI)) Comps.Player else Comps.MobAI
        return Entities.withComps(engine, defenderType).filter { CompMapper.State.get(it).current != BaseState.Dead }
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