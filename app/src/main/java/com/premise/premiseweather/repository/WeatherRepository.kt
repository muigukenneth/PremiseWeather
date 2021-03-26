package com.premise.premiseweather.repository


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.premise.premiseweather.dao.WeatherDao
import com.premise.premiseweather.entities.Weather
import com.premise.premiseweather.utils.Const


class WeatherRepository(private val weatherDao: WeatherDao) {
    fun getCurrentWeather(): LiveData<List<Weather>> {
        return weatherDao.getWeatherById(Const.CURRENT)
    }
    val get7dayWeather: DataSource.Factory<Int,Weather> = weatherDao.get7dayWeather(Const.CURRENT)
}