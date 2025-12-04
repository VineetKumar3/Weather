package com.example.weather.API

import com.google.gson.annotations.SerializedName

data class Current(
    @SerializedName("time")
    val time: String,
    @SerializedName("interval")
    val interval: Int,
    @SerializedName("temperature_2m")
    val temperature2m: Double,
    @SerializedName("wind_speed_10m")
    val windSpeed10m: Double,
    @SerializedName("weather_code")
    val weatherCode: Int
)
