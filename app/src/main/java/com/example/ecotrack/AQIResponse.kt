package com.example.ecotrack

data class AQIResponse(
    val list: List<AQIItem>
)

data class AQIItem(
    val main: AQIMain,
    val components: AQIComponents
)

data class AQIMain(
    val aqi: Int
)

data class AQIComponents(
    val pm2_5: Double,
    val pm10: Double
)