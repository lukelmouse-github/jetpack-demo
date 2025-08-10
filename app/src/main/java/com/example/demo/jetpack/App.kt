package com.example.demo.jetpack

import android.app.Application
import android.content.Context
import com.example.core.common.base.AppContext
import com.example.demo.jetpack.di.allModule
import com.tencent.mmkv.MMKV
import com.therouter.TheRouter
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // 初始化全局 AppContext，必须在最前面
        AppContext.init(this)

    }

    override fun onCreate() {
        super.onCreate()

        TheRouter.isDebug = true
        MMKV.initialize(this)

        startKoin {
            androidContext(this@App)
            modules(allModule)
        }

    }
}