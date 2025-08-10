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
    }

    override fun getLayoutResId(): Int = R.layout.activity_main
}