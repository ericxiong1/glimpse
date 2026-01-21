package com.example.glimpse.weather

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.glimpse.BuildConfig
import com.example.glimpse.fetchLocation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/*
FR10 - Weather.Widget
    Background worker to fetch weather information
 */

// THe following JSON classes are derived from the tutorial in the link below:
// https://dopebase.com/building-weather-app-kotlin-openweathermap-api
// JSON class for initial API response
@JsonClass(generateAdapter = true)
data class WeatherData(
    @Json(name = "main")
    val main: MainData,
    @Json(name = "weather")
    val weather: List<DescriptionData>,
    @Json(name = "name")
    val name: String
)

// JSON class for the 'main' subclass in the API response
@JsonClass(generateAdapter = true)
data class MainData(
    @Json(name = "temp")
    val temp: Double,
    @Json(name = "humidity")
    val humidity: Int,
    @Json(name = "feels_like")
    val feelsLike: Double,
)

// JSON class for the 'weather' subclass in the API response
@JsonClass(generateAdapter = true)
data class DescriptionData(
    @Json(name = "description")
    val description: String,
)

// Background worker to fetch weather information
class WeatherWorker(context: Context, workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {
    private val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    override suspend fun doWork(): Result {
        val location = fetchLocation(applicationContext) ?: return Result.retry()

        return try {
            val weatherData = location.let { fetchWeatherData(it.latitude, it.longitude) }
            if (weatherData != null) {
                saveWeatherData(sharedPreferences, weatherData)
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    // Query API for weather data at the passed coordinates
    //
    // Derived from following link:
    // https://dopebase.com/building-weather-app-kotlin-openweathermap-api
    private fun fetchWeatherData(lat: Double, long: Double): WeatherData? {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&appid=${BuildConfig.OPENWEATHERMAP_API_KEY}&units=metric"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        return try {
            val response: Response = client.newCall(request).execute()
            val jsonResponse = response.body?.string()

            if (response.isSuccessful && jsonResponse != null) {
                // structure API response into desired fields
                parseWeatherJson(jsonResponse)
            } else {
                Log.d("WeatherWorker", "Error fetching weather data: ${response.message}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveWeatherData(sharedPreferences: SharedPreferences, weatherData: WeatherData) {
        sharedPreferences.edit().apply {
            putString("temperature", weatherData.main.temp.toString())
            putString("description", weatherData.weather.firstOrNull()?.description ?: "")
            putString("name", weatherData.name)
            putString("feels_like", weatherData.main.feelsLike.toString())
            apply()
        }
    }

    // Converts single line JSON response to formatted class
    //
    // Derived from following link:
    // https://dopebase.com/building-weather-app-kotlin-openweathermap-api
    private fun parseWeatherJson(json: String): WeatherData {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(WeatherData::class.java)
        return adapter.fromJson(json) ?: throw JsonDataException("Invalid JSON format")
    }
}