package com.example.feature.login.network

/**
 * 网络请求基础模型
 */
data class BaseModel<T>(
    val data: T? = null,
    val errorCode: Int = 0,
    val errorMsg: String = ""
) {
    /**
     * 判断请求是否成功
     */
    fun isSuccess(): Boolean = errorCode == 0
}

