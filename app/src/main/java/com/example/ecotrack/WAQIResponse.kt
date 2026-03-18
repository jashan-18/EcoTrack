package com.jashan.ecotrack

data class WAQIResponse(
    val status: String,
    val data: WAQIData
)

data class WAQIData(
    val aqi: Int,
    val iaqi: IAQI
)

data class IAQI(
    val t: Value?,   // temperature
    val h: Value?    // humidity
)

data class Value(
    val v: Double
)