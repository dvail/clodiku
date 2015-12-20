package com.dvail.clodiku.world

import com.badlogic.ashley.core.Engine

class GameEngine() : Engine() {
    var saveLocation: String? = null
    var paused = false
}
