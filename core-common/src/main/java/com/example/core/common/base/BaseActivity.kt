package com.example.core.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.core.common.utils.StatusBarKt


abstract class BaseActivity<T : BaseViewModel, M : ViewDataBinding> : AppCompatActivity() {

    abstract val mViewModel: T
    lateinit var mViewBinding: M

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarKt.fitSystemBar(this)
        mViewBinding = DataBindingUtil.setContentView(this, getLayoutResId())

        initData()
        initView()

    }

    abstract fun initData()

    abstract fun initView()

    abstract fun getLayoutResId(): Int


}