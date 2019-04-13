package com.example.stackapp.utils;

import java.util.Date;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public Date getDateFromTimeStamp(String timeStamp){
        Long value = Long.parseLong(timeStamp);
        return value == null ? null : new Date(value);
    }

}
