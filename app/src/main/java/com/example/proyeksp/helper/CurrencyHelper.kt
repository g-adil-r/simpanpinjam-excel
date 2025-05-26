package com.example.proyeksp.helper

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class CurrencyHelper {
//    private var format: NumberFormat? = null
//
//    @JvmStatic
//    fun format(n: Long): String {
//        if (format == null) {
//            format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
//            format.setCurrency(Currency.getInstance("IDR"))
//            format.setMaximumFractionDigits(0)
//            format.setGroupingUsed(true)
//        }
//
//        return format!!.format(n)
//    }
    companion object {
        // Use the 'lazy' delegate for thread-safe, once-only initialization
        private val currencyFormatter: NumberFormat by lazy {
            // Locale "in" for Indonesian language, "ID" for Indonesia country
            val locale = Locale("in", "ID")
            NumberFormat.getCurrencyInstance(locale).apply {
                // Explicitly set currency for maximum clarity and robustness,
                // though getCurrencyInstance(locale) should infer it.
                currency = Currency.getInstance("IDR")
                maximumFractionDigits = 0
                isGroupingUsed = true // Use property access syntax for setters
            }
        }

    @JvmStatic
        fun format(amount: Long): String {
            return currencyFormatter.format(amount)
        }
    }
}