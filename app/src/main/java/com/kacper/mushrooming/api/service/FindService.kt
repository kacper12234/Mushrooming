package com.kacper.mushrooming.api.service

import com.kacper.mushrooming.api.dto.FindRequest
import com.kacper.mushrooming.api.dto.FindResponse
import com.kacper.mushrooming.api.dto.VisitRequest
import com.kacper.mushrooming.api.dto.VisitResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FindService {
    @GET("/api/finds/{lat},{lon}/{lat2},{lon2}")
    fun getFinds(@Path("lat") lat: Double, @Path("lat2") lat2: Double, @Path("lon") lon: Double, @Path("lon2") lon2: Double): Call<List<FindResponse>>

    @POST("/api/finds")
    fun saveFind(@Body findRequest: FindRequest): Call<FindResponse>

    @GET("/api/visits/{id}")
    fun getVisits(@Path("id") findId: Long): Call<List<VisitResponse>>

    @POST("/api/visits")
    fun saveVisit(@Body visitDto: VisitRequest): Call<VisitResponse>
}