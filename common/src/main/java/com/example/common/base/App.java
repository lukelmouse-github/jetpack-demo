package com.example.common.base;

import android.app.Application;
import android.content.Context;

import com.therouter.TheRouter;

public class App extends Application {
    public static App instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        instance = this;
        TheRouter.setDebug(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
