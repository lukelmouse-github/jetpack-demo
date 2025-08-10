package com.example.feature.login.manager

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.core.common.model.User
import com.example.feature.login.ui.LoginActivity
import com.tencent.mmkv.MMKV

/**
 * 用户管理类
 * 负责用户信息的存储、获取和状态管理
 */
object UserManager {

    private const val USER_DATA: String = "user_data"
    private var mmkv: MMKV = MMKV.defaultMMKV()

    private val liveData = MutableLiveData<User>()

    /**
     * 获取用户登录状态 LiveData
     */
    fun getLoginLiveData(): MutableLiveData<User> {
        return liveData
    }

    /**
     * 获取当前用户信息
     */
    fun getUser(): User? {
        return mmkv.decodeParcelable(USER_DATA, User::class.java)
    }

    /**
     * 保存用户信息
     */
    fun saveUser(user: User?) {
        mmkv.encode(USER_DATA, user)
        if (liveData.hasObservers()) {
            liveData.postValue(user)
        }
    }

    /**
     * 判断用户是否已登录
     */
    fun isLogin(): Boolean {
        return getUser() != null
    }

    /**
     * 移除用户信息（退出登录）
     */
    fun removeUser() {
        mmkv.encode(USER_DATA, "")
        if (liveData.hasObservers()) {
            liveData.postValue(null)
        }
    }

    /**
     * 启动登录页面
     */
    fun start(context: Context): LiveData<User> {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        return liveData
    }
}

