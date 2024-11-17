package com.example.common.base;

import android.app.Application;

import com.drake.logcat.LogCat;

public class App extends Application {
    public static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LogCat.INSTANCE.setTag("demo");
    }
}
