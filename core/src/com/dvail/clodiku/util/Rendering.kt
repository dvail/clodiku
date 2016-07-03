package com.dvail.clodiku.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.XmlReader
import com.dvail.clodiku.entities.BaseState
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.Direction
import java.util.*
import com.badlogic.gdx.utils.Array as GdxArray

fun makeTexture(src: String) : Texture = Texture(Gdx.files.internal(src))

fun makeRegions(srcDir: String): Pair<HashMap<String, HashMap<String, Animation>>, TextureAtlas> {
    var animations = HashMap<String, HashMap<String, Animation>>()

    val config = XmlReader().parse(FileHandle("${srcDir}anim.xml"))
    val atlas = TextureAtlas("$srcDir${config.get("pack")}")

    for (anim in config.getChildrenByName("sequence")) {
        val name = anim.getAttribute("name")
        val directions = anim.getChildrenByName("direction")

        animations.put(name, HashMap())

        for (view in directions) {
            val direction = view.getAttribute("name")
            val frameCount = view.getAttribute("frames")

            var speed = if (name.contains("melee", true)) 1/24f else 1/12f
            val animation = Animation(speed, getRegions(atlas, name, direction, frameCount.toInt()), Animation.PlayMode.LOOP)

            animations[name]?.put(direction, animation)
        }
    }

    return Pair(animations, atlas)
}

fun facingFromDirection(dir: Direction): String {
    return when (dir) {
        Direction.East -> "side"
        Direction.West -> "side"
        Direction.North -> "back"
        Direction.South -> "front"
        else -> "front"
    }
}

fun animFromState(state: BaseState): String = state.name.toLowerCase()

fun currentAnimation(entity: Entity): Animation? {
    var spatial = CompMapper.Spatial.get(entity)
    var state = CompMapper.State.get(entity)
    var animations = CompMapper.AnimatedRenderable.get(entity).animations
    var facing = facingFromDirection(spatial.direction)
    var animState = animFromState(state.current)

    return animations[animState]?.get(facing)
}

private fun getRegions(atlas: TextureAtlas, animName: String, direction: String, frameCount: Int): GdxArray<TextureRegion> {
    val regions = GdxArray<TextureRegion>(frameCount)

    for (i in 0..(frameCount - 1)) regions.add(atlas.findRegion("${animName}_$direction", i))

    return regions
}