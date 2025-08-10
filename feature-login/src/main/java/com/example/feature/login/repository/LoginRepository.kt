package com.example.feature.login.repository

import com.example.core.common.model.User
import com.example.core.net.client.RetrofitClient
import com.example.core.net.model.NetResult
import com.example.core.net.repository.BaseRepository
import com.example.feature.login.api.RequestCenter

/**
 * 登录数据仓库
 */
class LoginRepository : BaseRepository() {

    private val api by lazy { RetrofitClient.create<RequestCenter>() }

    /**
     * 用户登录
     */
    suspend fun login(username: String, password: String): NetResult<User> {
        return callRequest {
            handleResponse(api.login(username, password))
        }
    }

    /**
     * 用户注册
     */
    suspend fun register(
        username: String,
        password: String,
        surePassword: String
    ): NetResult<User> {
        return callRequest {
            handleResponse(api.register(username, password, surePassword))
        }
    }
}

