package com.dvail.klodiku.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

object BoundKeys {
    val MoveNorth = Input.Keys.W
    val MoveEast = Input.Keys.D
    val MoveSouth = Input.Keys.S
    val MoveWest = Input.Keys.A
    val MeleeAttack = Input.Keys.P
    val ToggleMenus = Input.Keys.TAB
    val GetItem = Input.Keys.L
}

fun keyPressed(key: Int) = Gdx.input.isKeyPressed(key)

fun keyJustPressed(key: Int) = Gdx.input.isKeyJustPressed(key)
