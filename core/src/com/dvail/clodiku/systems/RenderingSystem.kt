package com.dvail.clodiku.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Circle
import com.dvail.clodiku.combat.Weaponry
import com.dvail.clodiku.combat.getAttackers
import com.dvail.clodiku.entities.Carried
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.entities.Direction
import com.dvail.clodiku.events.EventQueue
import com.dvail.clodiku.events.EventType
import com.dvail.clodiku.events.MeleeHitEvent
import com.dvail.clodiku.events.SwapAreaEvent
import com.dvail.clodiku.util.Entities
import com.dvail.clodiku.util.asArray
import com.dvail.clodiku.util.currentAnimation
import com.dvail.clodiku.world.Maps
import java.util.*
import com.badlogic.gdx.utils.Array as GdxArray

class RenderingSystem(eventQ: EventQueue) : CoreSystem(eventQ) {
    val mapBackgroundLayers = intArrayOf(0, 1)
    val mapForegroundLayers = intArrayOf(2)

    val camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    val batch = SpriteBatch()
    val shapeRenderer = ShapeRenderer()
    val combatFont = BitmapFont()

    lateinit var renderableEntities : Array<Entity>
    lateinit var animatedEntities : Array<Entity>
    lateinit var spatialEntities : Array<Entity>

    val yIndexComparator = Comparator<Entity> { a, b ->
        val posA = CompMapper.Spatial.get(a).pos
        val posB = CompMapper.Spatial.get(b).pos
        (posB.y - posA.y).toInt()
    }

    lateinit var world: Engine
    lateinit var mapRenderer: OrthogonalTiledMapRenderer
    var delta = 0f

    override fun addedToEngine(engine: Engine) {
        world = engine
        mapRenderer = OrthogonalTiledMapRenderer(Maps.currentMap(world), batch)

        updateRenderableEntityList()
        updateAnimatedEntityList()
        updateSpatialEntityList()
    }

    override fun update(sysDelta: Float) {
        delta = sysDelta

        Gdx.gl.glClearColor(0f, 0f, 0.2f, 0.3f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        camera.position.set(Maps.getMapBounds(world, camera))

        eventQ.events[EventType.World]?.forEach {  event ->
            if (event is SwapAreaEvent && event.worldUpdated) {
                event.rendererUpdated = true
                updateMap()
            }
        }

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

    fun updateMap() {
        mapRenderer.map = Maps.currentMap(world)
        updateRenderableEntityList()
        updateAnimatedEntityList()
        updateSpatialEntityList()
    }

    private fun updateRenderableEntityList() {
        renderableEntities = Entities.withComps(world, Comps.Renderable, Comps.Spatial).asArray()
    }

    private fun updateAnimatedEntityList() {
        animatedEntities = Entities.withComps(world, Comps.AnimatedRenderable, Comps.Spatial).asArray()
    }

    private fun updateSpatialEntityList() {
        spatialEntities = Entities.withComps(world, Comps.Spatial).asArray()
    }

    private fun renderEntities() {
        renderableEntities.sortWith(yIndexComparator)

        for (ent in renderableEntities) {
            var currPos = CompMapper.Spatial.get(ent).pos

            if (currPos == Carried) continue

            var currTexture = CompMapper.Renderable.get(ent).texture
            batch.draw(currTexture, currPos.x - (currTexture.width / 2), currPos.y - (currTexture.height / 2))
        }
    }

    private fun renderAnimations() {
        animatedEntities.sortWith(yIndexComparator)

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
        val combatEvents = eventQ.events[EventType.Combat];

        combatEvents?.forEach { event ->
            when (event) {
                is MeleeHitEvent -> {
                    val time = event.time
                    val drawX = event.location.x
                    val drawY = event.location.y

                    combatFont.setColor(0.2f, 0.2f, 1f, (1 - (time / 2)))
                    combatFont.draw(batch, "poke", drawX, (25 + drawY + (100 * time)))
                }
            }
        }
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
            currWeapon = Weaponry.getWeapon(it)
            currCircle = CompMapper.EqWeapon.get(currWeapon).hitBox
            shapeRenderer.circle(currCircle.x, currCircle.y, currCircle.radius)
        }
    }
}