package com.example.proyeksp.helper

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyHelper {
    // Still better to make it a val if its configuration doesn't change after first init
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

//    private var format: NumberFormat? = null
//
//    fun format(n: Long): String {
//        if (format == null) {
//            format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
//            format.setCurrency(Currency.getInstance("IDR"))
//            format.setMaximumFractionDigits(0)
//            format.setGroupingUsed(true)
//        }
//
//        return format!!.format(n)
}