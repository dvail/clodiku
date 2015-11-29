package com.dvail.klodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Array as GdxArray
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Circle
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.util.*

class RenderingSystem : EntitySystem() {
    val mapBackgroundLayers = intArrayOf(0, 1)
    val mapForegroundLayers = intArrayOf(2)

    val camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    val batch = SpriteBatch()
    val shapeRenderer = ShapeRenderer()
    val combatFont = BitmapFont()

    var renderableEntities = ImmutableArray<Entity>(GdxArray(0))
    var animatedEntities = ImmutableArray<Entity>(GdxArray(0))
    var spatialEntities = ImmutableArray<Entity>(GdxArray(0))

    lateinit var world: Engine
    lateinit var mapRenderer: OrthogonalTiledMapRenderer

    override fun addedToEngine(engine: Engine) {
        world = engine;
        mapRenderer = OrthogonalTiledMapRenderer(currentMap(world), batch)
        updateRenderableEntityList()
        updateAnimatedEntityList()
        updateSpatialEntityList()
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
        renderAnimations()
        renderCombatVerbs()
        batch.end()

        shapeRenderer.setAutoShapeType(true)
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin()
        shapeRenderer.color = Color(0.5f, 1f, 0.5f, 1f)
        renderEntityShapes()
        shapeRenderer.color = Color(1f, 0.5f, 0.5f, 1f)
        renderCombatShapes()
        shapeRenderer.end()

        mapRenderer.setView(camera)
        mapRenderer.render(mapForegroundLayers)
    }

    private fun updateRenderableEntityList() {
        renderableEntities = entitiesWithComps(world, Comps.Renderable, Comps.Spatial)
    }

    private fun updateAnimatedEntityList() {
        animatedEntities = entitiesWithComps(world, Comps.AnimatedRenderable, Comps.Spatial)
    }

    private fun updateSpatialEntityList() {
        spatialEntities = entitiesWithComps(world, Comps.Spatial)
    }

    private fun renderEntities() {
        for (ent in renderableEntities) {
            var currPos = (compData(ent, CompMapper.Spatial) as Spatial).pos

            if (currPos == Carried) continue

            var currTexture = (compData(ent, CompMapper.Renderable) as Renderable).texture
            batch.draw(currTexture, currPos.x - (currTexture.width / 2), currPos.y - (currTexture.height / 2))
        }
    }

    private fun renderAnimations() {

        for (ent in animatedEntities) {
            var spatial = (compData(ent, CompMapper.Spatial) as Spatial)
            var state = (compData(ent, CompMapper.State) as State)
            var regions = (compData(ent, CompMapper.AnimatedRenderable) as AnimatedRenderable).regions
            var facing = facingFromDirection(spatial.direction)
            var animState = animFromState(state.current)

            var currTexture = regions[animState]?.get(facing)?.getKeyFrame(state.time)

            if (currTexture !== null) {
                var width = currTexture.regionWidth.toFloat()
                var height = currTexture.regionHeight.toFloat()
                var posX = spatial.pos.x - (width / 2)
                var posY = spatial.pos.y - (height / 2) + spatial.pos.radius + 3

                val flip = spatial.direction == Direction.West

                batch.draw(currTexture, if (flip) posX + width else posX, posY, if (flip) -width else width, height)
            }
        }
    }

    private fun renderCombatVerbs() {
    }

    private fun renderEntityShapes() {
        var currCircle: Circle

        for (ent in spatialEntities) {
            currCircle = (compData(ent, CompMapper.Spatial) as Spatial).pos
            if (currCircle == Carried) continue
            shapeRenderer.circle(currCircle.x, currCircle.y, currCircle.radius)
        }
    }

    private fun renderCombatShapes() {

    }
}