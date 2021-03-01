package com.kacper.mushrooming.api.service

import com.kacper.mushrooming.api.dto.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterService {
    @POST("/api/auth/signup")
    fun register(@Body request: RegisterRequest): Call<Void>
}