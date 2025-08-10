package com.example.feature.home.di

import com.example.feature.home.repository.HomeRepository
import com.example.feature.home.ui.home.HomeViewModel
import com.example.feature.home.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    // 注册 MainViewModel
    viewModel { MainViewModel() }

    // 注册 HomeRepository
    single { HomeRepository() }

    // 注册 HomeViewModel
    viewModel { HomeViewModel(get()) }
}

