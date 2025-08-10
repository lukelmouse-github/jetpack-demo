package com.example.feature.home.ui.main

import com.example.core.common.base.BaseActivity
import com.example.core.common.utils.ALog
import com.example.feature.home.R
import com.example.feature.home.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    // ✅ 使用 Koin 依赖注入，自动管理生命周期
    override val mViewModel: MainViewModel by viewModel()

    override fun initData() {
        // 初始化数据
        ALog.d("luke", "initData")
    }

    override fun initView() {
        // 初始化视图
        // 可以观察 ViewModel 中的 LiveData
        // mViewModel.someData.observe(this) { data ->
        //     // 处理数据变化
        // }
        ALog.d("luke", "initView")

        // 演示登录功能（点击测试按钮启动登录页面）
        setupLoginDemo()
    }

    /**
     * 设置登录功能演示
     */
    private fun setupLoginDemo() {
        // 为演示用的TextView添加点击事件
        mViewBinding.tvTest.setOnClickListener {
            // 使用LoginServiceWrapper启动登录页面
            com.example.core.common.service.LoginServiceWrapper.start(this)
        }

        // 监听用户登录状态变化
        try {
            val loginService = com.therouter.TheRouter.get(com.example.core.common.service.LoginService::class.java)

            loginService?.getLiveData()?.observe(this) { user ->
                if (user != null) {
                    ALog.d("luke", "用户登录成功: ${user.username}")
                    // 更新UI显示登录状态
                    mViewBinding.tvInfo.text = "欢迎, ${user.username}!"
                } else {
                    ALog.d("luke", "用户已退出登录")
                    mViewBinding.tvInfo.text = "点击上方按钮登录"
                }
            }
        } catch (e: Exception) {
            ALog.e("luke", "登录服务获取失败: ${e.message}")
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_main
}