package com.premise.premiseweather.utils

import java.text.DecimalFormat

object UnitsUtils {

    fun getDecimalFormat(quantity: Double): String {
        val decimalFormat = DecimalFormat("#.#")
        return decimalFormat.format(quantity)
    }
}