package com.dvail.clodiku.world

import com.badlogic.ashley.core.Engine

class GameEngine(gameSaveLoc: String) : Engine() {
    var saveLocation = gameSaveLoc
    var paused = false
}
