package com.example.feature.login.di

import com.example.feature.login.repository.LoginRepository
import com.example.feature.login.ui.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * 登录模块Koin配置
 */
val loginModule = module {

    // 注册 LoginRepository
    single {
        LoginRepository(get()) // get() 会自动获取 Retrofit 实例
    }

    // 注册 LoginViewModel
    viewModel {
        LoginViewModel(get()) // get() 会自动获取 LoginRepository 实例
    }
}

