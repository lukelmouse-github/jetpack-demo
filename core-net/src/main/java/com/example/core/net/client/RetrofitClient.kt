package com.example.core.net.client

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 客户端
 * 统一管理网络请求配置
 */
object RetrofitClient {

    private const val BASE_URL = "https://www.wanandroid.com/"

    /**
     * OkHttpClient 实例
     */
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Moshi 实例
     */
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Retrofit 实例
     */
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * 创建 API 服务
     * @param serviceClass API 服务接口类
     * @return API 服务实例
     */
    fun <T> create(serviceClass: Class<T>): T {
        return instance.create(serviceClass)
    }

    /**
     * 创建 API 服务 (Kotlin inline 版本)
     */
    inline fun <reified T> create(): T {
        return create(T::class.java)
    }
}

