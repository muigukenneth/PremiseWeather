package com.premise.premiseweather.dao


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.premise.premiseweather.entities.Weather
@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(access: Weather): Long

    @Update
    fun updateWeather(access: Weather)

    @Query("DELETE FROM Weather WHERE weatherId= :weatherId")
    fun deleteWeatherByUniqueId(weatherId: String): Int

    @Query("DELETE FROM Weather")
    fun deleteTable()

    @Query("SELECT * FROM Weather WHERE weatherId= :weatherId ORDER BY createdAt DESC")
    fun getWeatherById(weatherId: String): LiveData<List<Weather>>

    @Query("SELECT * FROM Weather WHERE weatherId != :current ORDER BY createdAt ASC limit 7")
    fun get7dayWeather(current: String): DataSource.Factory<Int, Weather>

}