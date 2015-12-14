package com.dvail.klodiku

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Screen
import com.dvail.klodiku.entities.DataLoader
import com.dvail.klodiku.events.EventQueue
import com.dvail.klodiku.systems.*
import com.dvail.klodiku.ui.GameUI

class MainScreen : Screen {

    val world = Engine()
    val eventQ = EventQueue()
    lateinit var gameUI: GameUI

    init {
        initMain(world)

        gameUI = GameUI(world, eventQ)

        world.addSystem(EventSystem(eventQ))
        world.addSystem(InputSystem(eventQ))
        world.addSystem(MobAISystem(eventQ))
        world.addSystem(CombatSystem(eventQ))
        world.addSystem(RenderingSystem(eventQ))
    }

    override fun render(delta: Float) {
        world.update(delta)
        gameUI.update(delta)
    }

    override fun pause() {
    }
    override fun resize(width: Int, height: Int) {
    }
    override fun hide() {
    }
    override fun resume() {
    }
    override fun dispose() {
    }
    override fun show() {
    }
}