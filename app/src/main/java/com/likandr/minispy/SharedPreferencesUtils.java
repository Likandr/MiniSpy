package com.likandr.minispy;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class SharedPreferencesUtils {
    private static String SharedPref = "ShPref";
    private static String KEY_ProcessList = "ProcessSet";

    private static Context sContext;
    private static Gson gson;

    private SharedPreferencesUtils() {
        throw new AssertionError("Utility class; do not instantiate.");
    }

    public static void initialize(Context context) {
        sContext = context.getApplicationContext();
        gson = new Gson();
    }

    private static void ensureContext() {
        if (sContext == null) {
            throw new IllegalStateException("Must call initialize(Context) before using methods in this class.");
        }
    }

    private static SharedPreferences getPreferences() {
        ensureContext();
        return sContext.getSharedPreferences(SharedPref, 0);
    }

    private static SharedPreferences.Editor getEditor() {
        return getPreferences().edit();
    }

    public static void storeProcessList(List<String> list){
        String string = getPreferences().getString(KEY_ProcessList, "");
        List<String> listTemp = strToList(string);

        if (listTemp.addAll(list)) {
            getEditor().putString(KEY_ProcessList, listToStr(listTemp)).apply();
        }
    }

    public static List<String> getProcessList(){
        String string = getPreferences().getString(KEY_ProcessList, "");
        return strToList(string);
    }

    public static void clearList() {
        getEditor().remove(KEY_ProcessList).apply();
    }

    private static String listToStr(List<String> list) {
        return gson.toJson(list);
    }

    private static List<String> strToList(String str) {
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> result = gson.fromJson(str, type);
        return result != null ? result : new ArrayList<String>();
    }
}
