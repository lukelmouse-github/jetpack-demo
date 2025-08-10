package com.example.core.net.repository

import com.example.core.net.exception.ExceptionHandler
import com.example.core.net.model.BaseModel
import com.example.core.net.model.NetResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

/**
 * 基础数据仓库类
 * 提供统一的网络请求处理逻辑
 */
open class BaseRepository {

    /**
     * 统一处理网络请求调用
     * @param call 网络请求函数
     * @return NetResult 封装的结果
     */
    suspend fun <T : Any> callRequest(
        call: suspend () -> NetResult<T>
    ): NetResult<T> {
        return try {
            call()
        } catch (e: Exception) {
            // 统一处理异常
            e.printStackTrace()
            NetResult.Error(ExceptionHandler.handleException(e))
        }
    }

    /**
     * 处理网络响应
     * @param response 网络响应
     * @param successBlock 成功回调
     * @param errorBlock 错误回调
     * @return NetResult 封装的结果
     */
    suspend fun <T : Any> handleResponse(
        response: BaseModel<T>,
        successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): NetResult<T> {
        return coroutineScope {
            if (response.isSuccess()) {
                successBlock?.let { it() }
                response.data?.let {
                    NetResult.Success(it)
                } ?: NetResult.Error(ExceptionHandler.handleException(RuntimeException("数据为空")))
            } else {
                errorBlock?.let { it() }
                NetResult.Error(ExceptionHandler.handleException(
                    RuntimeException("${response.errorCode}: ${response.errorMsg}")
                ))
            }
        }
    }

    /**
     * 简化版处理网络响应
     * @param response 网络响应
     * @return NetResult 封装的结果
     */
    suspend fun <T : Any> handleResponse(response: BaseModel<T>): NetResult<T> {
        return handleResponse(response, null, null)
    }
}

