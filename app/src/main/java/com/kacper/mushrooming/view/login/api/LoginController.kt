package com.kacper.mushrooming.view.login.api

import com.kacper.mushrooming.R
import com.kacper.mushrooming.api.ApiClient.createService
import com.kacper.mushrooming.api.dto.AuthenticationResponse
import com.kacper.mushrooming.api.dto.LoginRequest
import com.kacper.mushrooming.api.dto.RefreshTokenRequest
import com.kacper.mushrooming.api.service.AuthService
import com.kacper.mushrooming.utils.AuthenticationUtils.JSESSIONID
import com.kacper.mushrooming.utils.AuthenticationUtils.authenticationResponse
import com.kacper.mushrooming.view.login.LoginInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginController {
    private var service = createService(AuthService::class.java)
    private var timer = Timer()
    fun login(login: String, password: String, loginInterface: LoginInterface) {
        val loginRequest = LoginRequest(login, password)
        service.login(loginRequest).enqueue(object : Callback<AuthenticationResponse> {
            override fun onResponse(call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>) {
                if (response.isSuccessful) {
                    authenticationResponse = response.body()
                    JSESSIONID = response.headers()["Set-Cookie"]
                    loginInterface.storeCred(authenticationResponse, login, password, JSESSIONID)
                    loginInterface.login(login)
                    setTask(login, loginInterface)
                } else {
                    loginInterface.loginFail(R.string.login_failed_user)
                }
            }

            override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                t.printStackTrace()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        login(login, password, loginInterface)
                    }
                }, 60000)
                loginInterface.showSnackbar()
            }
        })
    }

    fun reconnect(login: String, token: String, sessionId: String, loginInterface: LoginInterface) {
        authenticationResponse = AuthenticationResponse()
        loginInterface.setToken(authenticationResponse)
        JSESSIONID = sessionId
        val request = RefreshTokenRequest(token, login)
        service.refreshToken(request).enqueue(object : Callback<AuthenticationResponse> {
            override fun onResponse(call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>) {
                if (response.isSuccessful) {
                    authenticationResponse = response.body()
                    loginInterface.storeCred(authenticationResponse, null, null, null)
                    loginInterface.login(login)
                    setTask(login, loginInterface)
                } else {
                    loginInterface.loginFail(R.string.login_failed)
                    authenticationResponse = null
                }
            }

            override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun refreshToken(login: String, token: String, loginInterface: LoginInterface) {
        val request = RefreshTokenRequest(token, login)
        service.refreshToken(request).enqueue(object : Callback<AuthenticationResponse> {
            override fun onResponse(call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>) {
                if (response.isSuccessful) {
                    authenticationResponse = response.body()
                    loginInterface.storeCred(authenticationResponse, null, null, null)
                    setTask(login, loginInterface)}
            }

            override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun setTask(login: String, loginInterface: LoginInterface) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                refreshToken(login, authenticationResponse!!.refreshToken!!, loginInterface)
            }
        }, Date.from(Instant.parse(authenticationResponse!!.expiresAt).minus(5, ChronoUnit.SECONDS)))
    }


   fun resetTask() {
        timer.cancel()
        timer.purge()
        timer = Timer()
    }
}