package com.premise.premiseweather.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import com.premise.premiseweather.R
import com.premise.premiseweather.diff.WeatherDiffCallback
import com.premise.premiseweather.entities.Weather
import com.premise.premiseweather.fragments.WeatherFragment
import com.premise.premiseweather.interfaces.ItemAdapterListener
import com.premise.premiseweather.utils.DateUtils
import com.premise.premiseweather.utils.GetWeatherDataUtils
import com.premise.premiseweather.utils.UnitsUtils
import com.premise.premiseweather.utils.ViewUtils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_weekly_weather.*

class WeatherAdapter(private val context: Context, activity: Fragment) :
    PagedListAdapter<Weather, WeatherAdapter.WeatherViewHolder>(
        WeatherDiffCallback()
    ) {

    private var mCallback: ItemAdapterListener? = null

    init {
        if (activity is WeatherFragment) {
            this.mCallback = activity
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_weekly_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val dataObject = getObject(position)
        if (dataObject != null) {
            holder.textViewTemp.text = UnitsUtils.getDecimalFormat(dataObject.temp).plus("\u00B0")
            holder.textViewDate.text =
               DateUtils.formatDate(dataObject.createdAt)
            holder.textViewForecast.text = dataObject.feel
            val generator = ColorGenerator.MATERIAL
            val color1 = generator.getColor(dataObject.weatherId)
            holder.cardViewContainer.setCardBackgroundColor(color1)
            Glide.with(context)
                .load(GetWeatherDataUtils.getFeaturedWeatherIcon(dataObject.icon))
                .transition(ViewUtils.requestAvatarTransitionOptions())
                .apply(ViewUtils.requestOptions(300, 300))
                .into(holder.imageViewWeather)
        }
        holder.itemView.setOnClickListener {
            mCallback!!.onClicked(holder.adapterPosition, false, it)
        }
        holder.itemView.setOnLongClickListener {
            mCallback?.onClicked(holder.adapterPosition, true, it)
            return@setOnLongClickListener true
        }
    }


    class WeatherViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    fun getObject(position: Int): Weather? {
        return getItem(position)
    }


}
