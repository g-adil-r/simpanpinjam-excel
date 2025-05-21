package com.example.proyeksp.helper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Locale

object DateHelper {
    val currentDateString: String
        get() {
            val current = GregorianCalendar.getInstance().time
            val formatter: DateFormat =
                SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
            return formatter.format(current)
        }
}
