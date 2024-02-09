package com.example.proyeksp.helper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyHelper {
    private static NumberFormat format;

    public static String format(long n) {
        if (format == null) {
            format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            format.setCurrency(Currency.getInstance("IDR"));
            format.setMaximumFractionDigits(0);
            format.setGroupingUsed(true);
        }

        return format.format(n);
    }
}
