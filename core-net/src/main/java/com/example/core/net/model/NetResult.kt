package com.example.core.net.model

/**
 * 网络请求结果封装
 */
sealed class NetResult<out T> {
    /**
     * 成功结果
     */
    data class Success<T>(val data: T) : NetResult<T>()

    /**
     * 错误结果
     */
    data class Error(val exception: ApiException) : NetResult<Nothing>()
}

/**
 * API异常封装
 */
data class ApiException(
    val code: Int,
    val msg: String
) : Exception(msg)

