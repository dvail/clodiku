package com.dvail.klodiku.util

import com.badlogic.ashley.core.Entity
import com.dvail.klodiku.entities.CompMapper
import com.dvail.klodiku.entities.Spatial

fun moveEntity(entity: Entity, movX: Float, movY: Float) {
    moveEntity(compData(entity, CompMapper.Spatial) as Spatial, movX, movY)
}

fun moveEntity(spatial: Spatial, movX: Float, movY: Float) {
    spatial.pos.x += movX
    spatial.pos.y += movY
}
