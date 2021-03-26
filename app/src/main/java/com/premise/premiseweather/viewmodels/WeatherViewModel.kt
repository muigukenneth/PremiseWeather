package com.premise.premiseweather.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.premise.premiseweather.databases.AppRoomDatabase
import com.premise.premiseweather.entities.Weather
import com.premise.premiseweather.repository.WeatherRepository

class WeatherViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: WeatherRepository
    var currentWeather: LiveData<List<Weather>>
    var get7dayWeather: LiveData<PagedList<Weather>>

    init {
        val weatherDao = AppRoomDatabase.getAppDataBase(application)!!.weatherDao()
        repository = WeatherRepository(weatherDao)
        currentWeather = MutableLiveData<List<Weather>>()
        val factoryWeather: DataSource.Factory<Int, Weather> = repository.get7dayWeather
        val pagedListBuilderWeather: LivePagedListBuilder<Int, Weather> = LivePagedListBuilder(
            factoryWeather,
            10
        )
        get7dayWeather = pagedListBuilderWeather.build()
    }

    fun getCurrentWeather() {
        currentWeather =
            repository.getCurrentWeather()
    }
}