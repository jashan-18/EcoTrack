package com.jashan.ecotrack



data class Coord(
    val lat: Double,
    val lon: Double
)

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val coord: Coord
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Weather(
    val description: String
)

data class Wind(
    val speed: Double
)
