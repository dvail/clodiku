package com.dvail.clodiku.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.dvail.clodiku.GameCore

object DesktopLauncher {
    @JvmStatic fun main(arg: Array<String>) {
        LwjglApplication(GameCore(), "Clodiku", 500, 300)
    }
}
