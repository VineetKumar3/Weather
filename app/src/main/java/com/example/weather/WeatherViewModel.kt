package com.example.weather

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather.API.NetworkResponse
import com.example.weather.API.RetrofitInstance
import com.example.weather.API.WeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

open class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    open val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    private val _locationName = MutableLiveData<String>()
    open var locationName: LiveData<String> = _locationName

    private val geocoder = Geocoder(application, Locale.getDefault())

    fun getData(latitude: Double, longitude: Double) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val locationName = withContext(Dispatchers.IO) {
                    try {
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        addresses?.firstOrNull()?.locality ?: "Current Location"
                    } catch (e: Exception) {
                        "Current Location"
                    }
                }
                _locationName.postValue(locationName)

                val response = weatherApi.getWeather(latitude, longitude)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.postValue(NetworkResponse.Success(it))
                    }
                } else {
                    _weatherResult.postValue(NetworkResponse.Error("Failed to load weather data"))
                }
            } catch (e: Exception) {
                _weatherResult.postValue(NetworkResponse.Error("Failed to load weather data"))
            }
        }
    }

    fun searchLocation(query: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.searchLocation(query)
                if (response.isSuccessful) {
                    response.body()?.results?.firstOrNull()?.let {
                        _locationName.postValue("${it.name}, ${it.countryCode}")
                        getData(it.latitude, it.longitude)
                    }
                } else {
                    _weatherResult.postValue(NetworkResponse.Error("Failed to find location"))
                }
            } catch (e: Exception) {
                _weatherResult.postValue(NetworkResponse.Error("Failed to find location"))
            }
        }
    }

    fun setError(message: String) {
        _weatherResult.value = NetworkResponse.Error(message)
    }

    fun setLoading() {
        _weatherResult.value = NetworkResponse.Loading
    }
}