package com.khpi.krykun.vitalii.utils;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {

    public static boolean isHoliday(Date date) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(date);
        return (startCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) ||
                (startCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
        //add logic of checking with list of national holidays
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
