package com.example.ecotrack

import com.jashan.ecotrack.WAQIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WAQIClient {

    val api: WAQIService by lazy {

        Retrofit.Builder()
            .baseUrl("https://api.waqi.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WAQIService::class.java)
    }
}