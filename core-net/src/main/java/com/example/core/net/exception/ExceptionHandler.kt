package com.example.core.net.exception

import com.example.core.net.model.ApiException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 网络异常处理器
 * 统一处理各种网络异常并转换为 ApiException
 */
object ExceptionHandler {

    /**
     * 处理异常，转换为 ApiException
     */
    fun handleException(e: Throwable): ApiException {
        return when (e) {
            is HttpException -> {
                ApiException(e.code(), "网络错误: ${e.message()}")
            }
            is ConnectException -> {
                ApiException(-1, "连接失败，请检查网络")
            }
            is UnknownHostException -> {
                ApiException(-1, "网络不可用，请检查网络设置")
            }
            is SocketTimeoutException -> {
                ApiException(-1, "连接超时，请稍后重试")
            }
            is IOException -> {
                ApiException(-1, "网络异常: ${e.message}")
            }
            is ApiException -> {
                e
            }
            else -> {
                ApiException(-1, e.message ?: "未知错误")
            }
        }
    }
}

