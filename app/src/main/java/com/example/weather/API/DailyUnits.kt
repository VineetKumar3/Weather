package com.example.weather.API

import com.google.gson.annotations.SerializedName

data class DailyUnits(
    @SerializedName("time")
    val time: String,
    @SerializedName("weather_code")
    val weatherCode: String,
    @SerializedName("temperature_2m_max")
    val temperature2mMax: String,
    @SerializedName("temperature_2m_min")
    val temperature2mMin: String
)
