package com.example.weather.API

import com.google.gson.annotations.SerializedName

data class Hourly(
    @SerializedName("time")
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temperature2m: List<Double>,
    @SerializedName("relative_humidity_2m")
    val relativeHumidity2m: List<Double>,
    @SerializedName("wind_speed_10m")
    val windSpeed10m: List<Double>,
    @SerializedName("pressure_msl")
    val pressureMsl: List<Double>,
    @SerializedName("weather_code")
    val weatherCode: List<Int>
)
