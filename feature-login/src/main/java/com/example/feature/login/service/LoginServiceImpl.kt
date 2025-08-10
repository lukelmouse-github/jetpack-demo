package com.example.feature.login.service

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.core.common.model.User
import com.example.core.common.service.LoginService
import com.example.feature.login.manager.UserManager
import com.therouter.inject.ServiceProvider
import com.therouter.inject.Singleton

/**
 * 登录服务具体实现类
 */

@Singleton
@ServiceProvider
class LoginServiceImpl : LoginService {

    override fun isLogin(): Boolean {
        return UserManager.isLogin()
    }

    override fun getUserInfo(): User? {
        return UserManager.getUser()
    }

    override fun removeUserInfo() {
        UserManager.removeUser()
    }

    override fun start(context: Context): LiveData<User> {
        return UserManager.start(context)
    }

    override fun getLiveData(): LiveData<User> {
        return UserManager.getLoginLiveData()
    }
}