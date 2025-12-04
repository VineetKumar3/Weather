package com.example.weather.API

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("elevation")
    val elevation: Double,
    @SerializedName("feature_code")
    val featureCode: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("admin1_id")
    val admin1Id: Int,
    @SerializedName("admin2_id")
    val admin2Id: Int,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("population")
    val population: Int,
    @SerializedName("country_id")
    val countryId: Int,
    @SerializedName("country")
    val country: String,
    @SerializedName("admin1")
    val admin1: String,
    @SerializedName("admin2")
    val admin2: String
)
