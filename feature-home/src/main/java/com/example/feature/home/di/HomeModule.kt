package com.example.feature.home.di

import com.example.feature.home.repository.HomeRepository
import com.example.feature.home.repository.ProjectRepository
import com.example.feature.home.ui.home.HomeViewModel
import com.example.feature.home.ui.main.MainViewModel
import com.example.feature.home.ui.project.ProjectViewModel
import com.example.feature.home.ui.project.TabItemViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    // 注册 MainViewModel
    viewModel { MainViewModel() }

    // 注册 HomeRepository
    single { HomeRepository() }

    // 注册 HomeViewModel
    viewModel { HomeViewModel(get()) }

    // 项目相关依赖注入
    // 注册 ProjectRepository
    single { ProjectRepository() }

    // 注册 ProjectViewModel
    viewModel { ProjectViewModel(get()) }

    // 注册 TabItemViewModel
    viewModel { TabItemViewModel(get()) }
}

