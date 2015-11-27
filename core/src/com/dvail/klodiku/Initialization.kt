package com.dvail.klodiku

import com.badlogic.ashley.core.Engine

private fun initMap(world: Engine, mapName: String) {
    initArea(world, "sample")
}

private fun initPlayer(world: Engine) {

}

private fun initArea(world: Engine, mapName: String) {
    print("Area $mapName initialized")
}

fun initMain(world: Engine){
    initPlayer(world)
    initMap(world, "sample")
}