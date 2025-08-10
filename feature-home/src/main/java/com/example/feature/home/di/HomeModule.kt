package com.example.feature.home.di

import com.example.feature.home.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    // 注册 MainViewModel
    viewModel { MainViewModel() }

    // 如果有 Repository 或其他依赖，也在这里注册
    // single<MainRepository> { MainRepositoryImpl(get()) }
}

