package com.premise.premiseweather.utils

import android.content.Context
import android.content.SharedPreferences


class AppPrefsHelper(private val mContext: Context) {
    val appsPrefs: SharedPreferences =
        mContext.getSharedPreferences(Const.MY_SHARED_PREF, Context.MODE_PRIVATE)

    val savedLocationName: String
        get() = getString(Const.SAVED_LOCATION_NAME, Const.EMPTY)!!

    val savedLatitude: String?
        get() = getString(Const.SAVED_LATITUDE, Const.EMPTY)

    val savedLongitude: String?
        get() = getString(Const.SAVED_LONGITUDE, Const.EMPTY)


    fun putString(key: String, value: String?) {
        appsPrefs.edit().putString(key, value).apply()
    }

    private fun getString(key: String, defaultValue: String?): String? {
        val globalValue = appsPrefs.getString(key, defaultValue)
        return appsPrefs.getString(key, globalValue)
    }

}
