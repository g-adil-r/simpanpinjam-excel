package com.example.proyeksp.helper

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyHelper {
    private val format: NumberFormat by lazy {
        NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
            currency = Currency.getInstance("IDR")
            maximumFractionDigits = 0
            isGroupingUsed = true
        }
    }

    fun format(n: Long): String {
        return format.format(n)
    }
}