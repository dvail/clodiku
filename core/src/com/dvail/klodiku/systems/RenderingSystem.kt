package com.dvail.klodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
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
import com.dvail.klodiku.combat.getAttackers
import com.dvail.klodiku.entities.*
import com.dvail.klodiku.events.EventQueue
import com.dvail.klodiku.util.*

class RenderingSystem(eventQ: EventQueue) : CoreSystem(eventQ) {
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
    var delta = 0f

    override fun addedToEngine(engine: Engine) {
        world = engine;
        mapRenderer = OrthogonalTiledMapRenderer(currentMap(world), batch)
        updateRenderableEntityList()
        updateAnimatedEntityList()
        updateSpatialEntityList()
    }

    override fun update(sysDelta: Float) {
        delta = sysDelta
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
            var currPos = CompMapper.Spatial.get(ent).pos

            if (currPos == Carried) continue

            var currTexture = CompMapper.Renderable.get(ent).texture
            batch.draw(currTexture, currPos.x - (currTexture.width / 2), currPos.y - (currTexture.height / 2))
        }
    }

    private fun renderAnimations() {

        for (ent in animatedEntities) {
            var spatial = CompMapper.Spatial.get(ent)
            var state = CompMapper.State.get(ent)
            var animation = currentAnimation(ent)

            var currTexture = animation?.getKeyFrame(state.time)

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
            currCircle = CompMapper.Spatial.get(ent).pos
            if (currCircle == Carried) continue
            shapeRenderer.circle(currCircle.x, currCircle.y, currCircle.radius)
        }
    }

    private fun renderCombatShapes() {
        var currCircle: Circle
        var currWeapon: Entity?
        val attackers = getAttackers(world)

        attackers.forEach { it ->
            currWeapon = CompMapper.Equipment.get(it).items[EqSlot.Held]
            currCircle = CompMapper.EqWeapon.get(currWeapon).hitBox
            shapeRenderer.circle(currCircle.x, currCircle.y, currCircle.radius)
        }
    }
}