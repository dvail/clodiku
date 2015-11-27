/**
 * Created by dave on 11/27/15.
 */
package com.dvail.klodiku

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.dvail.klodiku.entities.Spatial

class GameCore : ApplicationAdapter() {
    internal lateinit var batch: SpriteBatch
    internal lateinit var img: Texture

    override fun create() {
        batch = SpriteBatch()
        img = Texture("badlogic.jpg")

        val spatial: Spatial = Spatial(Vector2(2f, 3f), 12)
        spatial.size = 14

        print(spatial)
    }

    override fun render() {
        Gdx.gl.glClearColor(0.1f, 0f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        batch.draw(img, 0f, 0f)
        batch.end()
    }
}
