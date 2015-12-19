package com.dvail.clodiku

import com.badlogic.gdx.Game

class GameCore : Game() {
    override fun create() {
        setScreen(MainScreen())
    }
}
