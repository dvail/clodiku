package com.dvail.clodiku

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.dvail.clodiku.data.DataSaver
import com.dvail.clodiku.ui.UI

class StartScreen(mainGame: Game) : Screen {

    val game = mainGame
    val skin = Skin(Gdx.files.internal("./ui/uiskin.json"))
    val stage = Stage()
    val mainTable = Table()
    val options = Table()
    val savedGames = Table()
    val newGame = Label("New Game", skin, "default-font", Color(1f, 1f, 1f, 1f))
    val loadGame = Label("Load Game", skin, "default-font", Color(1f, 1f, 1f, 1f))

    init {
        Gdx.input.inputProcessor = stage

        options.add(newGame).row()
        options.add(loadGame).row()
        options.center()

        mainTable.debug = true
        mainTable.setFillParent(true)
        mainTable.center().pack()

        mainTable.add(options)

        UI.onClick(newGame, {
            this.dispose()
            game.screen = MainScreen(game)
        })

        UI.onClick(loadGame, {
            showSavedGames()
        })

        stage.addActor(mainTable)
    }

    private fun showSavedGames() {
        options.remove()

        DataSaver.getSavedGames().forEach { file ->
            val gameName = file.name
            val gameLabel = Label(gameName, skin, "default-font", Color(1f, 1f, 1f, 1f))

            UI.onClick(gameLabel, {
                this.dispose()
                game.screen = MainScreen(game, gameName)
            })

            savedGames.add(gameLabel).row()
        }

        val cancelLabel = Label("Cancel", skin, "default-font", Color(1f, 0f, 0.4f, 1f))

        UI.onClick(cancelLabel, {
            savedGames.remove()
            mainTable.add(options)
            savedGames.clear()
        })

        savedGames.add(cancelLabel)
        mainTable.add(savedGames)
    }

    override fun show() {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun hide() {
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0.2f, 0.1f, 0.3f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()
    }

    override fun resume() {
    }

    override fun dispose() {
        stage.dispose()
    }
}