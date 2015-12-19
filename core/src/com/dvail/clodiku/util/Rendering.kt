package com.dvail.clodiku.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.dvail.clodiku.entities.*
import com.badlogic.gdx.utils.Array as GdxArray
import com.eclipsesource.json.Json
import java.util.*

fun makeTexture(src: String) : Texture = Texture(Gdx.files.internal(src))

fun makeRegions(srcDir: String): HashMap<String, HashMap<String, Animation>> {
    var animations = HashMap<String, HashMap<String, Animation>>()
    var config = Gdx.files.internal("${srcDir}anim.json").readString()
    var json = Json.parse(config).asObject()
    val atlas = TextureAtlas("$srcDir${json.get("pack").asString()}")

    for (anim in json.get("animations").asArray()) {

        val animObj = anim.asObject()
        val name = animObj.get("name").asString()
        val animationViews = animObj.get("directions").asArray().zip(animObj.get("frames").asArray())

        animations.put(name, HashMap())

        for (view in animationViews) {
            val direction = view.first.asString()
            val frameCount = view.second.asInt()
            var speed = if (name.contains("melee", true)) 1/24f else 1/12f
            val animation = Animation(speed, getRegions(atlas, name, direction, frameCount), Animation.PlayMode.LOOP)

            animations[name]?.put(direction, animation)
        }
    }

    return animations
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