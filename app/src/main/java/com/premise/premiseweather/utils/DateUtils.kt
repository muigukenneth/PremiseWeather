package com.premise.premiseweather.utils
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatDate(date: Date): String {
        var final_date = Const.EMPTY
        // Get time from date
        val dateFormatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        final_date = dateFormatter.format(date)
        return final_date
    }

     fun getUTCToLocalTime(timeUTC:Long):Date{
         val calendar = Calendar.getInstance(TimeZone.getDefault())
         calendar.timeInMillis = timeUTC * 1000L
         return Date(calendar.timeInMillis)
    }
}
