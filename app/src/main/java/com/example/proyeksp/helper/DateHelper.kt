package com.example.proyeksp.helper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateHelper {
    companion object {
        val currentDateString: String
            get() {
                val current = GregorianCalendar.getInstance().time
                val formatter: DateFormat =
                    SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
                return formatter.format(current)
            }

        // Format an Instant object
        fun formatInstant(instant: Instant?): String {
            if (instant == null) return "-"

            val jInstant = instant.toJavaInstant()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.systemDefault())
            return formatter.format(jInstant)
        }
    }
}
