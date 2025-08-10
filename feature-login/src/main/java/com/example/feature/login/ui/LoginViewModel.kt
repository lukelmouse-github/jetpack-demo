package com.example.feature.login.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.common.base.BaseViewModel
import com.example.core.common.model.User
import com.example.core.common.utils.ToastUtils
import com.example.feature.login.network.NetResult
import com.example.feature.login.repository.LoginRepository
import kotlinx.coroutines.launch

/**
 * 登录视图模型
 */
class LoginViewModel(private val loginRepo: LoginRepository) : BaseViewModel() {

    private val loginLiveData = MutableLiveData<User>()

    /**
     * 用户登录
     */
    fun login(username: String, password: String): MutableLiveData<User> {
        viewModelScope.launch {
            val result = loginRepo.login(username, password)
            when (result) {
                is NetResult.Success -> {
                    loginLiveData.postValue(result.data)
                }
                is NetResult.Error -> {
                    ToastUtils.showToast(result.exception.msg)
                }
            }
        }
        return loginLiveData
    }

    /**
     * 用户注册
     */
    fun register(username: String, password: String, surePassword: String): MutableLiveData<User> {
        viewModelScope.launch {
            val result = loginRepo.register(username, password, surePassword)
            when (result) {
                is NetResult.Success -> {
                    loginLiveData.postValue(result.data)
                }
                is NetResult.Error -> {
                    ToastUtils.showToast(result.exception.msg)
                }
            }
        }
        return loginLiveData
    }
}

