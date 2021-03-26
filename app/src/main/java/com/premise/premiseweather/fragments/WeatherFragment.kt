package com.premise.premiseweather.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.premise.premiseweather.R
import com.premise.premiseweather.adapters.WeatherAdapter
import com.premise.premiseweather.dao.WeatherDao
import com.premise.premiseweather.databases.AppRoomDatabase
import com.premise.premiseweather.entities.Weather
import com.premise.premiseweather.interfaces.ItemAdapterListener
import com.premise.premiseweather.utils.*
import com.premise.premiseweather.utils.UnitsUtils
import com.premise.premiseweather.viewmodels.WeatherViewModel
import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.view_current_weather.*
import kotlinx.android.synthetic.main.view_loading.*
import kotlinx.android.synthetic.main.view_no_data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.CoroutineContext


class WeatherFragment : Fragment(), ItemAdapterListener, CoroutineScope {

    private var db: AppRoomDatabase? = null
    private var appPrefsHelper: AppPrefsHelper? = null
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var locationName: String = Const.EMPTY
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private var weatherDao: WeatherDao? = null
    private lateinit var weatherViewModel: WeatherViewModel
    private var mAdapter: WeatherAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppRoomDatabase.getAppDataBase(requireContext())
        weatherViewModel = ViewModelProviders.of(this)
            .get(WeatherViewModel::class.java)
        weatherDao = db?.weatherDao()
        appPrefsHelper = AppPrefsHelper(requireActivity())
        mLayoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = ScaleInLeftAnimator()
        mAdapter = WeatherAdapter(requireContext(), this@WeatherFragment)
        recyclerView.adapter = mAdapter
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            // refresh your list contents somehow
            startProgress()
            GetWeatherDataUtils.getCurrentWeatherApi(
                this,
                weatherDao,
                locationName,
                latitude,
                longitude
            )
        }
        cardViewLocation!!.setOnClickListener {
            val fields = listOf(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG
            )
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .build(context!!)
            startActivityForResult(intent, Const.RQC_AUTOCOMPLETE_REQUEST_CODE)
        }
        buttonGo!!.setOnClickListener {
            if(locationName.isNotEmpty()) {
                startProgress()
                GetWeatherDataUtils.getCurrentWeatherApi(
                    this,
                    weatherDao,
                    locationName,
                    latitude,
                    longitude
                )
            }
        }
        /**
         * Listen to live data
         */
        weatherViewModel.getCurrentWeather()
        weatherViewModel.currentWeather.observe(this, Observer { data ->
            // Update the cached copy of the words in the adapter.
            data?.let {
                if (it.isNotEmpty()) {
                    val weatherObject = data[0]
                    setValues(weatherObject)
                } else {
                    showNoData(false)
                }
            }
        })
        /**
         * Listen to live data 7 days
         */
        weatherViewModel.get7dayWeather.observe(viewLifecycleOwner, Observer { weather ->
            weather?.let {
                if (it.isNotEmpty()) {
                    ViewUtils.showView(textView7dayForecastHeader)
                } else {
                    ViewUtils.hideView(textView7dayForecastHeader)
                }
                mAdapter!!.submitList(it)
            }
        })
        setAnimatedUpButton()
        setLocation()

    }
    /**
     * Init Button animation
     */
    private fun setAnimatedUpButton() {
        buttonGo.attachTextChangeAnimator()
        bindProgressButton(buttonGo)
    }
    /**
     * Set location variables
     */
    private fun setLocation() {
        if (appPrefsHelper!!.savedLocationName.isNotEmpty()) {
            locationName = appPrefsHelper!!.savedLocationName
            if (appPrefsHelper!!.savedLatitude!!.isNotEmpty()) {
                latitude = appPrefsHelper!!.savedLatitude!!.toDouble()
            }
            if (appPrefsHelper!!.savedLongitude!!.isNotEmpty()) {
                longitude = appPrefsHelper!!.savedLongitude!!.toDouble()
            }
            textViewLocation.text = locationName
            GetWeatherDataUtils.getCurrentWeatherApi(
                this,
                weatherDao,
                locationName,
                latitude,
                longitude
            )
        } else {
            textViewLocation.text = resources.getString(R.string.pick_a_location)
        }
    }
    /**
     * Start progress
     */
    private fun startProgress() {
        ViewUtils.hideView(textViewDate)
        ViewUtils.hideView(textViewTemp)
        ViewUtils.hideView(textViewForecast)
        ViewUtils.hideView(linearLayoutOtherValues)
        ViewUtils.showView(shimmerFrameLayout)
        ViewUtils.hideView(linearLayoutNoData)
        buttonGo.isEnabled = false
        swipeRefreshLayout.isRefreshing = false
        ViewUtils.showSend(buttonGo)
    }
    /**
     * Stop progress
     */
    fun stopProgress() {
        ViewUtils.hideView(shimmerFrameLayout)
        buttonGo.isEnabled = true
        ViewUtils.resetSaved(context!!, buttonGo)
    }

    fun showNoData(error: Boolean) {
        ViewUtils.hideView(textViewDate)
        ViewUtils.hideView(textViewTemp)
        ViewUtils.hideView(textViewForecast)
        ViewUtils.hideView(linearLayoutOtherValues)
        ViewUtils.hideView(shimmerFrameLayout)
        ViewUtils.showView(linearLayoutNoData)
        YoYo.with(Techniques.Wave)
            .duration(1500)
            .playOn(roundedImageView)
        if (error) {
            textViewNoData.text = resources.getString(R.string.we_could_not_get_data)
        } else {
            textViewNoData.text = resources.getString(R.string.nothing_to_show)
        }

    }
    /**
     * Set values for weather
     */
    private fun setValues(weatherObject: Weather) {
        ViewUtils.showView(textViewDate)
        ViewUtils.showView(textViewTemp)
        ViewUtils.showView(textViewForecast)
        ViewUtils.showView(linearLayoutOtherValues)
        if (weatherObject.weatherId == Const.CURRENT) {
            textViewCurrentHeader.text = resources.getString(R.string.current)
        } else {
            textViewCurrentHeader.text = weatherObject.weatherId
        }
        textViewDate.text = DateUtils.formatDate(weatherObject.createdAt)
        textViewTemp.text =
            UnitsUtils.getDecimalFormat(weatherObject.temp).plus("\u00B0")
        textViewForecast.text = weatherObject.feel
        textViewHumidity.text =
            String.format(
                "%s : %s", resources.getString(R.string.humidity),
                UnitsUtils.getDecimalFormat(weatherObject.humidity)
            ).plus("%")
        textViewPressure.text =
            String.format(
                "%s : %s", resources.getString(R.string.pressure),
                UnitsUtils.getDecimalFormat(weatherObject.pressure)
            ).plus(" hPa")
        textViewWind.text =
            String.format(
                "%s : %s", resources.getString(R.string.wind),
                UnitsUtils.getDecimalFormat(weatherObject.wind)
            ).plus(" mph")
        textViewPop.text =
            String.format(
                "%s : %s", resources.getString(R.string.precipitation),
                UnitsUtils.getDecimalFormat(weatherObject.pop)
            ).plus("%")
        if (weatherObject.pop > 0) {
            ViewUtils.showView(textViewPop)
        } else {
            ViewUtils.hideView(textViewPop)
        }
        Glide.with(this).load(GetWeatherDataUtils.getFeaturedWeatherIcon(weatherObject.icon))
            .transition(ViewUtils.requestAvatarTransitionOptions())
            .apply(ViewUtils.requestOptions(300, 300))
            .into(imageViewWeather)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.RQC_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    locationName = place.name!!
                    latitude = place.latLng!!.latitude
                    longitude = place.latLng!!.longitude
                    appPrefsHelper!!.putString(Const.SAVED_LOCATION_NAME, locationName)
                    appPrefsHelper!!.putString(Const.SAVED_LATITUDE, latitude.toString())
                    appPrefsHelper!!.putString(Const.SAVED_LONGITUDE, longitude.toString())
                    textViewLocation.text = locationName

                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    val status =
                        Autocomplete.getStatusFromIntent(data!!)
                    NotifyUtils.showErrorToast(
                        context!!,
                        status.statusMessage!!
                    )
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                    NotifyUtils.showErrorToast(
                        context!!,
                        getString(R.string.please_finish_process)
                    )
                }
            }
        }
    }

    override fun onClicked(index: Int, longClick: Boolean, v: View) {
        val weather = mAdapter!!.getObject(index)
        if (!longClick) {
            if (weather != null) {
                setValues(weather)
            }
        }
    }

    companion object {

        private val TAG = WeatherFragment::class.java.simpleName
        fun newInstance(): WeatherFragment {
            val fragment = WeatherFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
