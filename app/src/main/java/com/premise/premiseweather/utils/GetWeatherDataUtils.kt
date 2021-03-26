package com.premise.premiseweather.utils

import android.net.Uri
import android.util.Log
import com.premise.premiseweather.R
import com.premise.premiseweather.dao.WeatherDao
import com.premise.premiseweather.entities.Weather
import com.premise.premiseweather.fragments.WeatherFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


/**
 * Created by Kenneth Waweru on 28/02/2017.
 */

object GetWeatherDataUtils {
    /**
     * Get the weather for open weather API
     */
    fun getCurrentWeatherApi(
        fragment: WeatherFragment,
        weatherDao: WeatherDao?,
        locationName: String,
        latitude: Double,
        longitude: Double
    ) {

        fragment.launch(Dispatchers.IO) {
            val builder: Uri.Builder = Uri.Builder()
            builder.scheme(Const.HTTPS)
                .authority(Const.URL_GET_CURRENT_WEATHER)
                .appendPath(Const.DATA)
                .appendPath(Const.VERSION_2)
                .appendPath(Const.ONE_CALL)
                .appendQueryParameter(Const.LAT, latitude.toString())
                .appendQueryParameter(Const.LON, longitude.toString())
                .appendQueryParameter(Const.UNITS, Const.IMPERIAL)
                .appendQueryParameter(
                    Const.APP_ID,
                    fragment.getString(R.string.open_weather_api_key)
                )
            val myUrl: String = builder.build().toString()
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(myUrl)
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    if (response.code == 200) {
                        val dataObject = JSONObject(response.body!!.string())
                        Log.d(TAG, "response received$dataObject")
                        val current = dataObject.get(Const.CURRENT) as JSONObject
                        val dt = current.get(Const.DT).toString().toLong()
                        val temp = current.get(Const.TEMP).toString().toDouble()
                        val pressure = current.get(Const.PRESSURE).toString().toDouble()
                        val humidity = current.get(Const.HUMIDITY).toString().toDouble()
                        val windSpeed = current.get(Const.WIND_SPEED).toString().toDouble()
                        val weather = current.get(Const.WEATHER) as JSONArray
                        val dataObjectWeather = weather[0] as JSONObject
                        val feel = dataObjectWeather.get(Const.MAIN) as String
                        val description = dataObjectWeather.get(Const.DESCRIPTION) as String
                        val icon = dataObjectWeather.get(Const.ICON) as String
                        val date=DateUtils.getUTCToLocalTime(dt)
                        val weatherInsert = Weather(
                            Const.CURRENT,
                            feel,
                            description,
                            temp,
                            0.toDouble(),
                            0.toDouble(),
                            humidity,
                            pressure,
                            windSpeed,
                            locationName,
                            latitude,
                            longitude,
                            icon,
                            0.toDouble(),
                            date
                        )
                        weatherDao!!.insertWeather(weatherInsert)
                        val daily = dataObject.get(Const.DAILY) as JSONArray
                        for (i in 0 until daily.length()) {
                            val dataObjectDaily = daily[i] as JSONObject
                            val dtDaily = dataObjectDaily.get(Const.DT).toString().toLong()
                            val tempDaily =dataObjectDaily.get(Const.TEMP) as JSONObject
                            val day = tempDaily.get(Const.DAY).toString().toDouble()
                            val min = tempDaily.get(Const.MIN).toString().toDouble()
                            val max = tempDaily.get(Const.MIN).toString().toDouble()
                            val pressureDaily =
                                dataObjectDaily.get(Const.PRESSURE).toString().toDouble()
                            val humidityDaily =
                                dataObjectDaily.get(Const.HUMIDITY).toString().toDouble()
                            val popDaily =
                                dataObjectDaily.get(Const.POP).toString().toDouble()
                            val windSpeedDaily =
                                dataObjectDaily.get(Const.WIND_SPEED).toString().toDouble()
                            val weatherDaily = dataObjectDaily.get(Const.WEATHER) as JSONArray
                            val dataObjectWeatherDaily = weatherDaily[0] as JSONObject
                            val feelDaily = dataObjectWeatherDaily.get(Const.MAIN) as String
                            val descriptionDaily =
                                dataObjectWeatherDaily.get(Const.DESCRIPTION) as String
                            val iconDaily = dataObjectWeatherDaily.get(Const.ICON) as String
                            val dateDaily=DateUtils.getUTCToLocalTime(dtDaily)
                            val weatherDailyInsert = Weather(
                                getDayName(dateDaily),
                                feelDaily,
                                descriptionDaily,
                                day,
                                min,
                                max,
                                humidityDaily,
                                pressureDaily,
                                windSpeedDaily,
                                locationName,
                                latitude,
                                longitude,
                                iconDaily,
                                popDaily,
                                dateDaily
                            )
                            weatherDao.insertWeather(weatherDailyInsert)
                        }

                        launch(Dispatchers.Main) {
                            fragment.stopProgress()
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            fragment.showNoData(true)
                            NotifyUtils.showErrorToast(
                                fragment.requireContext(),
                                fragment.resources.getString(R.string.we_could_not_get_data)
                            )
                        }
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    fragment.showNoData(true)
                    NotifyUtils.showErrorToast(
                        fragment.requireContext(),
                        fragment.resources.getString(R.string.we_could_not_get_data)
                    )
                }
            }
        }
    }

    fun getFeaturedWeatherIcon(OWMIconCode: String?): String {
        return when (OWMIconCode) {
            "01d" -> Const.ART_CLEAR
            "01n" -> Const.ART_CLEAR
            "02d", "02n" -> Const.ART_LIGHT_CLOUDS
            "03d", "03n" -> Const.ART_CLOUDS
            "04d", "04n" -> Const.ART_LIGHT_CLOUDS
            "09d", "09n" -> Const.ART_RAIN
            "10d", "10n" -> Const.ART_LIGHT_RAIN
            "11d", "11n" -> Const.ART_STORM
            "13d", "13n" -> Const.ART_SNOW
            "50d", "50n" -> Const.ART_FOG
            else -> Const.ART_CLEAR
        }
    }

    private fun getDayName(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        when (cal[Calendar.DAY_OF_WEEK]) {
            Calendar.MONDAY -> {
                return Const.MONDAY
            }
            Calendar.TUESDAY -> {
                return Const.TUESDAY
            }
            Calendar.WEDNESDAY -> {
                return Const.WEDNESDAY
            }
            Calendar.THURSDAY -> {
                return Const.THURSDAY
            }
            Calendar.FRIDAY -> {
                return Const.FRIDAY
            }
            Calendar.SATURDAY -> {
                return Const.SATURDAY
            }
            Calendar.SUNDAY -> {
                return Const.SUNDAY
            }
        }
        return Const.EMPTY
    }

    private val TAG = GetWeatherDataUtils::class.java.simpleName

}
