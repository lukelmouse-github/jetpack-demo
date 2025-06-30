package com.example.demo;

import android.app.Application;
import android.content.Context;

import com.example.common.base.App;
import com.therouter.TheRouter;

public class DemoApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        App.INSTANCE.setInstance(this);
        TheRouter.setDebug(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
