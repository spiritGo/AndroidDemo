package com.example.spirit.androiddemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

    final public static String WEATHER_INFO = "weather_info";
    final public static String CITY = "city";
    final public static String FAILED = "failed";
    final public static String ERROR = "error";
    final private static String NAME = "info.txt";
    final private static SharedPreferences SP = Util.getMContext().getSharedPreferences(NAME,
            Context.MODE_PRIVATE);

    public static void putString(String key, String value) {
        SP.edit().putString(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        return SP.getString(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        SP.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return SP.getBoolean(key, defValue);
    }
}
