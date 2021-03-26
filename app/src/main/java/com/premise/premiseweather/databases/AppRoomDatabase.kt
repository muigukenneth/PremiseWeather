package com.premise.premiseweather.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.premise.premiseweather.dao.WeatherDao
import com.premise.premiseweather.entities.Weather
import com.premise.premiseweather.typeconverters.DateTypeConverter


@Database(
    entities = [Weather::class],
    version = 3, exportSchema = false
)
@TypeConverters(
    DateTypeConverter::class
)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        var INSTANCE: AppRoomDatabase? = null
        fun getAppDataBase(context: Context): AppRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(AppRoomDatabase::class) {
                    INSTANCE =
                        Room.databaseBuilder(
                            context.applicationContext,
                            AppRoomDatabase::class.java,
                            "premiseWeatherDB"
                        )
                            .fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}
