package com.dvail.klodiku

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.dvail.klodiku.systems.*

class MainScreen : Screen {

    val world = Engine()

    internal lateinit var batch: SpriteBatch
    internal lateinit var img: Texture

    init {
        world.addSystem(Events())
        world.addSystem(Input())
        world.addSystem(MobAI())
        world.addSystem(Combat())
        world.addSystem(Rendering())

        batch = SpriteBatch()
        img = Texture("badlogic.jpg")

        initMain(world)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.1f, 0f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        batch.draw(img, 0f, 0f)
        batch.end()
        world.update(delta)
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