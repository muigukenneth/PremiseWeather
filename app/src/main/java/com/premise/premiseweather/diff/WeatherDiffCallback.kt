package com.premise.premiseweather.diff

import androidx.recyclerview.widget.DiffUtil
import com.premise.premiseweather.entities.Weather

class WeatherDiffCallback  : DiffUtil.ItemCallback<Weather>() {
    override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
        return oldItem.weatherId == newItem.weatherId
    }

    override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
        return oldItem == newItem
    }
}