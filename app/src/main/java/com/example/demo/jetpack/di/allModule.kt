package com.example.demo.jetpack.di

import com.example.feature.home.di.homeModule
import com.example.feature.login.di.loginModule
import com.google.gson.GsonBuilder
import org.koin.dsl.module

val otherModule = module {
    // Gson (如果需要的话)
    single {
        GsonBuilder().disableHtmlEscaping().create()
    }
}

val allModule = listOf(
    otherModule,
    homeModule,
    loginModule
)