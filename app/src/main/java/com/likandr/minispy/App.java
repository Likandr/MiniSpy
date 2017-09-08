package com.likandr.minispy;

import android.app.Application;

import com.likandr.minispy.utils.SharedPreferencesUtils;

public class App extends Application {

    private static App sInstance;
    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        SharedPreferencesUtils.initialize(sInstance);
    }
}
