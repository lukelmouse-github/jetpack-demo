package com.example.feature.login.ui

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.feature.login.BR

/**
 * 登录页面状态管理类
 */
class LoginLayoutBean : BaseObservable() {

    var tips: String = "密码登录"
        get() {
            field = if (isLogin) {
                "密码登录"
            } else {
                "账号注册"
            }
            return field
        }

    var btnText: String = "登录"
        get() {
            field = if (isLogin) {
                "登录"
            } else {
                "注册"
            }
            return field
        }

    var featureName: String = "注册"
        get() {
            field = if (isLogin) {
                "注册"
            } else {
                "登录"
            }
            return field
        }

    @get:Bindable
    var isLogin: Boolean = true
        set(value) {
            if (value == field) {
                return
            }
            field = value
            notifyPropertyChanged(BR._all)
        }
}

