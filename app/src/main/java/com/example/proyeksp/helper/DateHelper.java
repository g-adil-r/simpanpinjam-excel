package com.example.proyeksp.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateHelper {
    public static String getCurrentDateString() {
        Date current = GregorianCalendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        return formatter.format(current);
    }
}
