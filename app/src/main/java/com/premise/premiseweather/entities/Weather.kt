package com.premise.premiseweather.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(primaryKeys = ["weatherId"], indices = [Index(value = ["weatherId"],
    unique = true)])
@Parcelize
data class Weather(
    val weatherId: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val feel: String,
    val description: String,
    val temp: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val humidity: Double,
    val pressure: Double,
    val wind: Double,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val icon: String,
    val pop: Double,
    val createdAt: Date
) : Parcelable

