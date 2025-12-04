package com.example.weather.API

import com.google.gson.annotations.SerializedName

data class LocationSearchModel(
    @SerializedName("results")
    val results: List<Location>,
    @SerializedName("generationtime_ms")
    val generationtimeMs: Double
)
