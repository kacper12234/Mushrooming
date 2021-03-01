package com.kacper.mushrooming.api.service

import com.kacper.mushrooming.api.dto.AuthenticationResponse
import com.kacper.mushrooming.api.dto.LoginRequest
import com.kacper.mushrooming.api.dto.RefreshTokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<AuthenticationResponse>

    @POST("/api/auth/refresh/token")
    fun refreshToken(@Body request: RefreshTokenRequest): Call<AuthenticationResponse>

    @POST("/api/auth/logout")
    fun logout(@Body request: RefreshTokenRequest): Call<Void>
}