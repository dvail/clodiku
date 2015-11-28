package com.dvail.klodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.util.compData
import com.dvail.klodiku.util.currentMap
import com.dvail.klodiku.util.entitiesWithComps
import com.dvail.klodiku.util.getMapBounds

class RenderingSystem : EntitySystem() {
    val mapBackgroundLayers = intArrayOf(0, 1)
    val mapForegroundLayers = intArrayOf(2)

    val camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    val batch = SpriteBatch()
    val shapeRenderer = ShapeRenderer()
    val combatFont = BitmapFont()

    var renderableEntities = ImmutableArray<Entity>(com.badlogic.gdx.utils.Array(0))

    lateinit var world: Engine
    lateinit var mapRenderer: OrthogonalTiledMapRenderer

    override fun addedToEngine(engine: Engine) {
        world = engine;
        mapRenderer = OrthogonalTiledMapRenderer(currentMap(world), batch)
        updateEntityList()
    }

    override fun update(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 0.3f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        camera.position.set(getMapBounds(world, camera))

        mapRenderer.setView(camera)
        mapRenderer.render(mapBackgroundLayers)

        batch.begin()
        batch.projectionMatrix = camera.combined
        renderEntities()
        renderCombatVerbs()
        batch.end()

        mapRenderer.setView(camera)
        mapRenderer.render(mapForegroundLayers)
    }

    fun updateEntityList() {
        renderableEntities = entitiesWithComps(world, Comps.Renderable, Comps.Spatial)
    }

    fun renderEntities() {
        var currPos: Vector2
        var currTexture: Texture

        for (ent in renderableEntities) {
            currPos = (compData(ent, CompMapper.Spatial) as Spatial).pos

            if (currPos == Carried) continue

            currTexture = (compData(ent, CompMapper.Renderable) as Renderable).texture
            batch.draw(currTexture, currPos.x - (currTexture.width / 2), currPos.y - (currTexture.height / 2))
        }
    }

    fun renderCombatVerbs() {

    }
}