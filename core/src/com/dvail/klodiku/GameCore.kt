package com.dvail.klodiku

import com.badlogic.gdx.Game

class GameCore : Game() {
    override fun create() {
        setScreen(MainScreen())
    }
}
