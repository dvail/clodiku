package com.dvail.clodiku.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

object BoundKeys {
    val MoveNorth = Input.Keys.W
    val MoveEast = Input.Keys.D
    val MoveSouth = Input.Keys.S
    val MoveWest = Input.Keys.A
    val Strafe = Input.Keys.SHIFT_LEFT
    val MeleeAttack = Input.Keys.P
    val ToggleMenus = Input.Keys.TAB
    val GetItem = Input.Keys.L
    val Pause = Input.Keys.ESCAPE
}

fun keyPressed(key: Int) = Gdx.input.isKeyPressed(key)

fun keyJustPressed(key: Int) = Gdx.input.isKeyJustPressed(key)
