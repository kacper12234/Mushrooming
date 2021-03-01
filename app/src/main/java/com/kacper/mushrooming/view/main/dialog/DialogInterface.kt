package com.kacper.mushrooming.view.main.dialog

interface DialogInterface {
    fun addFind(species: String, lat: Double, lon: Double)
    fun addVisit(state: String, count: Int, findId: Long)
}