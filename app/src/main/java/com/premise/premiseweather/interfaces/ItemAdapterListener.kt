package com.premise.premiseweather.interfaces

import android.view.View

interface ItemAdapterListener {
    fun onClicked(index: Int, longClick: Boolean, v: View)
}
