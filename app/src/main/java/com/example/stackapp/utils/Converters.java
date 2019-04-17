package com.example.stackapp.utils;

import android.text.Html;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import androidx.room.TypeConverter;

public class Converters {

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "Q");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }


    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }


    public static String toFormattedDateTime(String timeStamp){

        if(timeStamp!=null && !timeStamp.isEmpty()){
            Long value = Long.parseLong(timeStamp);
            // convert to miliseconds
            Date date = new Date(value * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("dd LLL, yyyy 'at' HH:mm");
            return sdf.format(date);

        }
        return " ";
    }

    public static String toFormattedTitle(String title){
        return  Html.fromHtml(title).toString();

    }




}
