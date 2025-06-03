package com.example.localist.database;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class MapTypeConverter {
    @TypeConverter
    public static Map<String, String> fromString(String value) {
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        return new Gson().fromJson(value, type);
    }

    @TypeConverter
    public static String fromMap(Map<String, String> map) {
        return new Gson().toJson(map);
    }
}
