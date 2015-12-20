package com.dvail.clodiku.file

import java.io.File
import java.util.*

object FileUtils {
    const val SAVE_DIR_BASE = "/.clodiku/game-saves"
    const val DEFAULT_SAVE_DIR_PREFIX = "/default-save"
    val userHome = System.getProperty("user.home")

    fun newSaveDirectory(): String {
        val baseSaveDir = File(userHome + SAVE_DIR_BASE)
        baseSaveDir.mkdirs()

        var saveDir = File(userHome + SAVE_DIR_BASE + DEFAULT_SAVE_DIR_PREFIX)
        var saveDirSuffix = 1

        while (saveDir.exists()) {
            saveDir = File(userHome + SAVE_DIR_BASE + DEFAULT_SAVE_DIR_PREFIX + saveDirSuffix.toString())
            saveDirSuffix++
        }

        saveDir.mkdirs()

        return saveDir.absolutePath
    }

    fun getSavedGames(): Array<out File> {
        val saves = ArrayList<String>()

        return File(userHome + SAVE_DIR_BASE).listFiles()
    }
}