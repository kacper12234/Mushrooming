package com.kacper.mushrooming.api

import com.kacper.mushrooming.utils.AuthenticationUtils.JSESSIONID
import com.kacper.mushrooming.utils.AuthenticationUtils.authenticationResponse
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class TokenInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val authenticationResponse = authenticationResponse
        var newRequest = chain.request()
        if (authenticationResponse != null) {
            newRequest = newRequest.newBuilder()
                    .header("Authorization", "Bearer " + authenticationResponse.authenticationToken)
                    .header("Cookie", JSESSIONID!!)
                    .method(newRequest.method(), newRequest.body())
                    .build()
        }
        println(newRequest.headers())
        println(newRequest.url())
        return chain.proceed(newRequest)
    }
}