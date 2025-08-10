package com.example.feature.login.ui

import androidx.lifecycle.Observer
import com.example.core.common.base.BaseActivity
import com.example.core.common.model.User
import com.example.feature.login.R
import com.example.feature.login.databinding.ActivityLoginBinding
import com.example.feature.login.manager.UserManager
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 登录页面
 */
class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    override val mViewModel: LoginViewModel by viewModel()

    private lateinit var mData: LoginLayoutBean

    override fun getLayoutResId(): Int = R.layout.activity_login

    override fun initData() {
        mData = LoginLayoutBean()
        mViewBinding.bean = mData
    }

    override fun initView() {
        // 确保隐藏 ActionBar
        supportActionBar?.hide()

        // 关闭按钮点击事件
        mViewBinding.close.setOnClickListener {
            finish()
        }

        // 切换登录/注册模式
        mViewBinding.featureName.setOnClickListener {
            mData.isLogin = !mData.isLogin
            initEditText()
        }

        // 登录/注册按钮点击事件
        mViewBinding.btnLogin.setOnClickListener {
            if (mData.isLogin) {
                // 登录
                loginAction()
            } else {
                // 注册
                registerAction()
            }
        }
    }

    /**
     * 执行登录操作
     */
    private fun loginAction() {
        mViewModel.login(getUserName(), getPassword()).observe(this, Observer { user ->
            saveUserInfo(user)
        })
    }

    /**
     * 执行注册操作
     */
    private fun registerAction() {
        mViewModel.register(getUserName(), getPassword(), getSurePassword())
            .observe(this, Observer { user ->
                saveUserInfo(user)
            })
    }

    /**
     * 保存用户信息并关闭页面
     */
    private fun saveUserInfo(user: User?) {
        UserManager.saveUser(user)
        finish()
    }

    /**
     * 获取用户名
     */
    private fun getUserName(): String {
        return mViewBinding.userName.text.toString().trim()
    }

    /**
     * 获取密码
     */
    private fun getPassword(): String {
        return mViewBinding.password.text.toString().trim()
    }

    /**
     * 获取确认密码
     */
    private fun getSurePassword(): String {
        return mViewBinding.surePassword.text.toString().trim()
    }

    /**
     * 初始化输入框（切换模式时清空）
     */
    private fun initEditText() {
        mViewBinding.userName.text = null
        mViewBinding.password.text = null
        mViewBinding.surePassword.text = null
    }
}

