package com.premise.premiseweather
import android.app.Application
import com.google.android.libraries.places.api.Places



class AppController : Application() {

    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        /**
         * Init the Places API
         */
        Places.initialize(applicationContext, this.getString(R.string.google_places_api_key))

    }

    companion object {
        val TAG = AppController::class.java
            .simpleName
        @get:Synchronized
        lateinit var instance: AppController
    }
}
