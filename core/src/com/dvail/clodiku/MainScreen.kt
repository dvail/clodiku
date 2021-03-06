package com.dvail.clodiku

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.dvail.clodiku.events.EventQueue
import com.dvail.clodiku.file.DataLoader
import com.dvail.clodiku.file.DataSaver
import com.dvail.clodiku.file.FileUtils
import com.dvail.clodiku.systems.*
import com.dvail.clodiku.ui.GameUICore
import com.dvail.clodiku.world.GameEngine

class MainScreen(val game: Game, savedGame: String? = null) : Screen {
    val eventQ = EventQueue()
    var gameUI: GameUICore
    var world: GameEngine

    init {
        val newGame = savedGame == null
        val saveLocation = savedGame ?: FileUtils.newSaveDirectory()
        val dataLoader = DataLoader()
        val dataSaver = DataSaver()

        world = GameEngine(saveLocation, dataLoader, dataSaver)

        if (newGame) {
            dataSaver.initPlayerFile(saveLocation)
        }

        val currentArea = dataLoader.savedPlayerArea(saveLocation)

        world.loadPlayer(saveLocation)
        world.loadMap(currentArea)
        world.loadArea(currentArea)

        gameUI = GameUICore(this.game, world, eventQ)

        world.addSystem(EventSystem(eventQ))
        world.addSystem(InputSystem(eventQ))
        world.addSystem(MobAISystem(eventQ))
        world.addSystem(CombatSystem(eventQ))
        world.addSystem(TickSystem(eventQ))
        world.addSystem(RenderingSystem(eventQ))
    }

    override fun render(delta: Float) {
        world.gameTime = world.gameTime + delta
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