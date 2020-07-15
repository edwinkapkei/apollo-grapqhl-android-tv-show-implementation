package com.edwinkapkei.tvshows.utilities

import java.text.SimpleDateFormat
import java.util.*

class Utilities {

    fun stringDateToYear(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateString)
            val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
            //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
            dateFormat.format(date)
        } catch (ignored: Exception) {
            dateString
        }
    }
}