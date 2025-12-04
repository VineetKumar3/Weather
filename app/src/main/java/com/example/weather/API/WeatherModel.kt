package com.example.weather.API

import com.google.gson.annotations.SerializedName

data class WeatherModel(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("generationtime_ms")
    val generationtimeMs: Double,
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    @SerializedName("elevation")
    val elevation: Double,
    @SerializedName("current")
    val current: Current,
    @SerializedName("hourly_units")
    val hourlyUnits: HourlyUnits,
    @SerializedName("hourly")
    val hourly: Hourly,
    @SerializedName("daily_units")
    val dailyUnits: DailyUnits,
    @SerializedName("daily")
    val daily: Daily
)
