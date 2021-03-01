package com.kacper.mushrooming.view.register

interface RegisterInterface {
    fun register()
    fun registerFail()
    fun dataAlreadyUsed(message : String)
}