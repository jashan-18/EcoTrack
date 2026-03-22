package com.jashan.ecotrack

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WAQIService {

    @GET("feed/{city}/")
    fun getCityAQI(
        @Path("city") city: String,
        @Query("token") token: String
    ): Call<WAQIResponse>

    @GET("feed/{city}/")
    fun getAQI(
        @Path("city") city: String,
        @Query("token") token: String
    ): Call<WAQIResponse>
}