package com.kacper.mushrooming.view.register.api

import com.kacper.mushrooming.api.ApiClient
import com.kacper.mushrooming.api.dto.RegisterRequest
import com.kacper.mushrooming.api.service.RegisterService
import com.kacper.mushrooming.view.register.RegisterInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterController {
    var service = ApiClient.createService(RegisterService::class.java)
    fun register(login: String, password: String, email: String, name: String, surname: String, registerInterface: RegisterInterface) {
        val request = RegisterRequest(name, surname, login, password, email)
        service.register(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when {
                    response.isSuccessful -> registerInterface.register()
                    response.code() == 409 -> registerInterface.dataAlreadyUsed(response.message())
                    else -> registerInterface.registerFail()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
                registerInterface.registerFail()
            }
        })
    }
}