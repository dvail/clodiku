package com.dvail.clodiku

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.dvail.clodiku.events.EventQueue
import com.dvail.clodiku.systems.*
import com.dvail.clodiku.ui.GameUICore
import com.dvail.clodiku.world.GameEngine

class MainScreen(mainGame: Game) : Screen {

    val game = mainGame
    val world = GameEngine()
    val eventQ = EventQueue()
    lateinit var gameUI: GameUICore

    init {
        initMain(world)

        gameUI = GameUICore(game, world, eventQ)

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
        println("Clean up all game resources here!")
    }
    override fun show() {
    }
}