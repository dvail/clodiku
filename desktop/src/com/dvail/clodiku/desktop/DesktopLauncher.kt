package com.dvail.clodiku.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.dvail.clodiku.GameCore

object DesktopLauncher {
    @JvmStatic fun main(arg: Array<String>) {
        val cfg = LwjglApplicationConfiguration()

        cfg.title = "Clodiku"
        cfg.width = 600
        cfg.height = 400

        LwjglApplication(GameCore(), cfg)
    }
}
