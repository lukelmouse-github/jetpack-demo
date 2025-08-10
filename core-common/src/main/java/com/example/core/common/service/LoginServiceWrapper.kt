package com.example.core.common.service

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.core.common.model.User
import com.therouter.TheRouter

/**
 * 登录服务包装类
 * 提供给其他模块使用的登录功能
 */
object LoginServiceWrapper {

    private val loginService: LoginService? by lazy {
        try {
            TheRouter.get(LoginService::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 判断用户是否已登录
     */
    fun isLogin(): Boolean {
        return loginService?.isLogin() ?: false
    }

    /**
     * 获取当前用户信息
     */
    fun getUserInfo(): User? {
        return loginService?.getUserInfo()
    }

    /**
     * 移除用户信息（退出登录）
     */
    fun removeUserInfo() {
        loginService?.removeUserInfo()
    }

    /**
     * 启动登录页面
     * @param context 上下文
     * @return 用户登录状态的 LiveData，如果服务不可用则返回空的 LiveData
     */
    fun start(context: Context): LiveData<User>? {
        return loginService?.start(context)
    }

    /**
     * 获取用户登录状态的 LiveData
     */
    fun getLiveData(): LiveData<User>? {
        return loginService?.getLiveData()
    }
}

