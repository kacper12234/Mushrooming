package com.kacper.mushrooming.view.login

import com.kacper.mushrooming.api.dto.AuthenticationResponse

interface LoginInterface {
    fun login(login: String?)
    fun storeCred(response: AuthenticationResponse?, login: String?, password: String?, sessionId: String?)
    fun loginFail(string: Int)
    fun setToken(response: AuthenticationResponse?)
    fun showSnackbar()
}