package com.example.cmps297nmedicationreminder.logic;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Helper {

    public static long getDateDiff(Date date1, Date date2) {
        long diffInMilliseconds = date2.getTime() - date1.getTime();
        return TimeUnit.HOURS.convert(diffInMilliseconds,TimeUnit.MILLISECONDS);
    }

    public static long getDateDiffInMinutes(Date date1, Date date2) {
        long diffInMilliseconds = date2.getTime() - date1.getTime();
        return TimeUnit.MINUTES.convert(diffInMilliseconds,TimeUnit.MILLISECONDS);
    }

    public static Date getDate(int year, int month, int datOfMonth){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, datOfMonth);
        Date date = cal.getTime();
        return date;
    }
    public static Date getDate(int hour, int minutes){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int datOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, datOfMonth,hour,minutes);
        Date date = cal.getTime();
        return date;
    }
    public static Date getDate(int year, int month, int datOfMonth, int hours, int minutes){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, datOfMonth,hours,minutes);
        Date date = cal.getTime();
        return date;
    }
    public static Date getCurrentDate(int year, int month, int datOfMonth, int hours, int minutes){
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        return date;
    }
}
