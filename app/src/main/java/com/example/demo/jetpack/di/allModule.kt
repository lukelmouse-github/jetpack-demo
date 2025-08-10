package com.example.demo.jetpack.di


import com.example.feature.home.di.homeModule
import org.koin.dsl.module



val otherModule = module {
//
//    single {
//        RetrofitClient.instance
//    }
//
//    single {
//        GsonBuilder().disableHtmlEscaping().create()
//    }
}


val allModule = listOf(
    otherModule,
    homeModule,
//    treeRepoModule, treeViewModelModule,
//    detailRepoModule, detailViewModelModule,
//    loginRepoModule, loginViewModelModule,
//    searchRepoModule, searchViewModelModule

)