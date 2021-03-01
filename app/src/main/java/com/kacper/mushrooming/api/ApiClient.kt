package com.kacper.mushrooming.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val URL = "https://mushrooming.azurewebsites.net"
    private val interceptor = TokenInterceptor()
    private val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    private val retrofitInstance: Retrofit.Builder
        get() = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URL)
                .client(client)

    fun <S> createService(
            serviceClass: Class<S>?): S {
        return retrofit().create(serviceClass)
    }

    fun retrofit(): Retrofit {
        return retrofitInstance.build()
    }
}