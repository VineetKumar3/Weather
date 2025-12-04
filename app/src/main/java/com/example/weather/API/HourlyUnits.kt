package com.example.weather.API

import com.google.gson.annotations.SerializedName

data class HourlyUnits(
    @SerializedName("time")
    val time: String,
    @SerializedName("temperature_2m")
    val temperature2m: String,
    @SerializedName("relative_humidity_2m")
    val relativeHumidity2m: String,
    @SerializedName("wind_speed_10m")
    val windSpeed10m: String,
    @SerializedName("pressure_msl")
    val pressureMsl: String,
    @SerializedName("weather_code")
    val weatherCode: String
)
