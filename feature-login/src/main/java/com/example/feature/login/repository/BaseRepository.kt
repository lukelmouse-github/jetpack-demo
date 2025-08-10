package com.example.feature.login.repository

import com.example.feature.login.network.ApiException
import com.example.feature.login.network.BaseModel
import com.example.feature.login.network.NetResult

/**
 * 基础数据仓库类
 */
open class BaseRepository {

    /**
     * 处理网络请求调用
     */
    suspend fun <T> callRequest(call: suspend () -> NetResult<T>): NetResult<T> {
        return try {
            call()
        } catch (e: Exception) {
            NetResult.Error(ApiException(-1, e.message ?: "网络请求失败"))
        }
    }

    /**
     * 处理网络响应
     */
    suspend fun <T> handleResponse(response: BaseModel<T>): NetResult<T> {
        return if (response.isSuccess()) {
            response.data?.let {
                NetResult.Success(it)
            } ?: NetResult.Error(ApiException(-1, "数据为空"))
        } else {
            NetResult.Error(ApiException(response.errorCode, response.errorMsg))
        }
    }
}

