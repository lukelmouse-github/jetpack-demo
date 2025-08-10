package com.example.core.common.service

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.core.common.model.User

/**
 * 登录服务接口
 * 提供登录相关功能给其他模块调用
 */
interface LoginService {

    /**
     * 判断用户是否已登录
     */
    fun isLogin(): Boolean

    /**
     * 获取当前用户信息
     */
    fun getUserInfo(): User?

    /**
     * 移除用户信息（退出登录）
     */
    fun removeUserInfo()

    /**
     * 启动登录页面
     * @param context 上下文
     * @return 用户登录状态的 LiveData
     */
    fun start(context: Context): LiveData<User>

    /**
     * 获取用户登录状态的 LiveData
     */
    fun getLiveData(): LiveData<User>
}

