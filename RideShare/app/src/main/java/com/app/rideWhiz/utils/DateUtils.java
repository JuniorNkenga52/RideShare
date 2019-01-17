package com.app.rideWhiz.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String getFormattedTime(String time) {

        try {
            SimpleDateFormat srcDf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.getDefault());
            return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(srcDf.parse(time));
        } catch (ParseException e) {
            return time;
        }
    }

    public static String parseDate(int Year, int Month, int day) {

        String sYear = String.valueOf(Year);
        String sMonth = String.valueOf((Month + 1));
        String sDay = String.valueOf(day);

        if (sDay.length() == 1)
            sDay = "0" + sDay;

        if (sMonth.length() == 1)
            sMonth = "0" + sMonth;

        return sYear + "-" + sMonth + "-" + sDay;
    }

    public static String dateformat(String time) {
        String inputPattern = "yyyy-MM-dd hh:mm:ss";
        String outputPattern = "EEE, dd MMM, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static long dateToMilli(String pattern, String date_time) {

        final SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());

        try {
            return sdf.parse(date_time).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public static Date setDateFix(String date) {

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date d1 = sdf.parse(date);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(d1);
            cal1.set(Calendar.DAY_OF_MONTH, cal1.get(Calendar.DAY_OF_MONTH) + 1);
            return cal1.getTime();
        } catch (Exception e) {
            try {
                return sdf.parse(date);
            } catch (ParseException e1) {
                return null;
            }
        }
    }

    public static String getCurrentDate(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }

    public static long getDiffInDay(String date1, String date2) {
        long diff = dateToMilli("yyyy MM dd", date1) - dateToMilli("yyyy MM dd", date2);
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static String convertDate(String inputPattern, String outputPattern, String dateTime) {

        try {
            SimpleDateFormat srcDf = new SimpleDateFormat(inputPattern, Locale.getDefault());
            Date date = srcDf.parse(dateTime);
            SimpleDateFormat destDf = new SimpleDateFormat(outputPattern, Locale.getDefault());
            return destDf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    public static ArrayList<Date> getAllDates(String dateString1, String dateString2) {

        ArrayList<Date> dates = new ArrayList<>();

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }

        return dates;
    }

    public static String dateToString(String pattern, Date date) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }
}