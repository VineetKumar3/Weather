package com.example.weather

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.example.weather.API.NetworkResponse
import com.example.weather.API.WeatherModel
import com.example.weather.preview.MockWeatherViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(viewModel: WeatherViewModel) {

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun fetchWeatherForCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            viewModel.setLoading()
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { location ->
                    location?.let {
                        viewModel.getData(it.latitude, it.longitude)
                    } ?: viewModel.setError("Could not retrieve location. Please ensure location services are enabled.")
                }
                .addOnFailureListener { exception ->
                    viewModel.setError("Failed to get location: ${exception.message}")
                }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                fetchWeatherForCurrentLocation()
            } else {
                viewModel.setError("Location permission denied. Please grant permission to see the weather for your current location.")
            }
        }
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fetchWeatherForCurrentLocation()
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.Transparent.toArgb()
            WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = false
        }
    }

    var city by remember {
        mutableStateOf("")
    }
    var isSearchActive by remember { mutableStateOf(false) }

    val weatherResult = viewModel.weatherResult.observeAsState()
    val locationName = viewModel.locationName.observeAsState()

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A3A7C), Color(0xFF6A5A99), Color(0xFF947DB4))
    )

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val searchBarTextStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                CompositionLocalProvider(LocalTextStyle provides searchBarTextStyle) {
                    SearchBar(
                        query = city,
                        onQueryChange = { city = it },
                        onSearch = {
                            viewModel.searchLocation(it)
                            isSearchActive = false
                        },
                        active = isSearchActive,
                        onActiveChange = { isSearchActive = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(30.dp)), // Apply border via modifier
                        placeholder = { Text("Search for any location", color = Color.Gray, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search for location",
                                tint = Color.White
                            )
                        },
                        trailingIcon = {
                            if (isSearchActive) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close search bar",
                                    tint = Color.White,
                                    modifier = Modifier.clickable {
                                        if (city.isNotEmpty()) {
                                            city = ""
                                        } else {
                                            isSearchActive = false
                                        }
                                    }
                                )
                            }
                        },
                        colors = SearchBarDefaults.colors(
                            containerColor = Color.Transparent,
                            inputFieldColors = SearchBarDefaults.inputFieldColors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                cursorColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                unfocusedPlaceholderColor = Color.Gray,
                                focusedPlaceholderColor = Color.Gray
                            ),
                            dividerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(30.dp),
                    ) {
                        // Content for the search results dropdown can be placed here
                    }
                }

                when (val result = weatherResult.value) {
                    is NetworkResponse.Error -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = result.message,
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    NetworkResponse.Loading -> {
                        Spacer(modifier = Modifier.height(32.dp))
                        CircularProgressIndicator(color = Color.White)
                        Text("Fetching weather...", color = Color.White, modifier = Modifier.padding(top = 16.dp))
                    }
                    is NetworkResponse.Success<*> -> {
                        WeatherDetails(data = result.data as WeatherModel, locationName.value ?: "")
                    }
                    null -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color.White)
                            Text("Waiting for location permission...", color = Color.White, modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDetails(data: WeatherModel, locationName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Location and Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = locationName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Text(
            text = formatDate(data.current.time, "EEEE, MMMM dd, HH:mm"),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.5f))

        // Main Weather Info
        AsyncImage(
            model = getWeatherIconUrl(data.current.weatherCode),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(160.dp).padding(vertical = 8.dp)
        )
        Text(
            text = "${data.current.temperature2m}°C",
            fontSize = 72.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = "Feels like: ${data.current.temperature2m}°C",
            fontSize = 20.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = getWeatherStatus(data.current.weatherCode),
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.5f))

        // Additional Details
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            WeatherDetailRow(label = "Humidity", value = "${data.hourly.relativeHumidity2m.first()}% ")
            WeatherDetailRow(label = "Wind", value = "${data.current.windSpeed10m} km/h")
            WeatherDetailRow(label = "Pressure", value = "${data.hourly.pressureMsl.first()} hPa")
        }
    }
}

@Composable
fun WeatherDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Color.LightGray)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Color.White)
    }
}

private fun formatDate(dateString: String, format: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat(format, Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (_: Exception) {
        dateString
    }
}

fun getWeatherIconUrl(weatherCode: Int): String {
    return when (weatherCode) {
        0 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/clear-day.png"
        1, 2, 3 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/partly-cloudy-day.png"
        45, 48 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/fog.png"
        51, 53, 55 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/drizzle.png"
        56, 57 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/sleet.png"
        61, 63, 65 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/rain.png"
        66, 67 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/sleet.png"
        71, 73, 75 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/snow.png"
        77 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/snow.png"
        80, 81, 82 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/showers-day.png"
        85, 86 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/snow.png"
        95 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/thunderstorm.png"
        96, 99 -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/thunderstorm.png"
        else -> "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/2nd%20Set%20-%20Color/clear-day.png"
    }
}

fun getWeatherStatus(weatherCode: Int): String {
    return when (weatherCode) {
        0 -> "Clear sky"
        1, 2, 3 -> "Mainly clear, partly cloudy, and overcast"
        45, 48 -> "Fog and depositing rime fog"
        51, 53, 55 -> "Drizzle: Light, moderate, and dense intensity"
        56, 57 -> "Freezing Drizzle: Light and dense intensity"
        61, 63, 65 -> "Rain: Slight, moderate and heavy intensity"
        66, 67 -> "Freezing Rain: Light and heavy intensity"
        71, 73, 75 -> "Snow fall: Slight, moderate, and heavy intensity"
        77 -> "Snow grains"
        80, 81, 82 -> "Rain showers: Slight, moderate, and violent"
        85, 86 -> "Snow showers slight and heavy"
        95 -> "Thunderstorm: Slight or moderate"
        96, 99 -> "Thunderstorm with slight and heavy hail"
        else -> "Unknown"
    }
}

@Composable
@Preview(showBackground = true)
fun WeatherPagePreview() {
    MaterialTheme {
        WeatherPage(viewModel = MockWeatherViewModel(LocalContext.current.applicationContext as Application))
    }
}
