package com.premise.premiseweather.utils

import android.content.Context
import android.widget.Toast

object NotifyUtils {

    fun showErrorToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
