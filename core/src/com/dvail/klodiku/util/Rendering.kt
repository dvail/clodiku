package com.dvail.klodiku.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

fun makeTexture(src: String) : Texture = Texture(Gdx.files.internal(src))
