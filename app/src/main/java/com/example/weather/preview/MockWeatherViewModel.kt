package com.example.weather.preview

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather.API.Current
import com.example.weather.API.Daily
import com.example.weather.API.DailyUnits
import com.example.weather.API.Hourly
import com.example.weather.API.HourlyUnits
import com.example.weather.API.NetworkResponse
import com.example.weather.API.WeatherModel
import com.example.weather.WeatherViewModel

class MockWeatherViewModel(application: Application) : WeatherViewModel(application) {

    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>(
        NetworkResponse.Success(
            WeatherModel(
                latitude = 34.05,
                longitude = -118.24,
                generationtimeMs = 0.0,
                utcOffsetSeconds = 0,
                timezone = "America/Los_Angeles",
                timezoneAbbreviation = "PST",
                elevation = 0.0,
                current = Current(
                    time = "2023-03-20T12:00",
                    interval = 0,
                    temperature2m = 25.5,
                    windSpeed10m = 10.0,
                    weatherCode = 2
                ),
                hourlyUnits = HourlyUnits(
                    time = "iso8601",
                    temperature2m = "°C",
                    relativeHumidity2m = "%",
                    windSpeed10m = "km/h",
                    pressureMsl = "hPa",
                    weatherCode = "wmo code"
                ),
                hourly = Hourly(
                    time = listOf("2023-03-20T12:00"),
                    temperature2m = listOf(25.5),
                    relativeHumidity2m = listOf(60.0),
                    windSpeed10m = listOf(10.0),
                    pressureMsl = listOf(1016.0),
                    weatherCode = listOf(2)
                ),
                dailyUnits = DailyUnits(
                    time = "iso8601",
                    weatherCode = "wmo code",
                    temperature2mMax = "°C",
                    temperature2mMin = "°C"
                ),
                daily = Daily(
                    time = listOf("2023-03-20"),
                    weatherCode = listOf(2),
                    temperature2mMax = listOf(28.0),
                    temperature2mMin = listOf(22.0)
                )
            )
        )
    )
    override val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    override var locationName: LiveData<String> = MutableLiveData("Los Angeles, US")

}
