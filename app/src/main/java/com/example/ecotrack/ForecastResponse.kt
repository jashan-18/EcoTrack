package com.example.ecotrack

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt_txt: String,
    val main: MainForecast,
    val weather: List<WeatherForecast>
)

data class MainForecast(
    val temp: Double,
    val humidity: Int
)

data class WeatherForecast(
    val description: String
)