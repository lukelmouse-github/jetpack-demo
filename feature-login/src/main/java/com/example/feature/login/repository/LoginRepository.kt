package com.example.feature.login.repository

import com.example.core.common.model.User
import com.example.feature.login.api.RequestCenter
import com.example.feature.login.network.NetResult
import retrofit2.Retrofit

/**
 * 登录数据仓库
 */
class LoginRepository(private val retrofit: Retrofit) : BaseRepository() {

    private val api by lazy { retrofit.create(RequestCenter::class.java) }

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

