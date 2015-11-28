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

    init {
        initMain(world)

        world.addSystem(EventSystem())
        world.addSystem(InputSystem())
        world.addSystem(MobAISystem())
        world.addSystem(CombatSystem())
        world.addSystem(RenderingSystem())
    }

    override fun render(delta: Float) {
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