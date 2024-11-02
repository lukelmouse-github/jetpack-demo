package com.example.common.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.drake.engine.base.EngineFragment
import com.drake.logcat.LogCat
import com.therouter.TheRouter

abstract class BaseFragment<B : ViewDataBinding>(@LayoutRes contentLayoutId: Int = 0) : EngineFragment<B>(contentLayoutId) {
    override fun initView() {
        LogCat.d(this.javaClass.simpleName + " initView")
    }

    override fun initData() {
        LogCat.d(this.javaClass.simpleName + " initData")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TheRouter.inject(this)
        LogCat.d(this.javaClass.simpleName + " onCreate")
    }

    override fun onStop() {
        super.onStop()
        LogCat.d(this.javaClass.simpleName + " onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogCat.d(this.javaClass.simpleName + " onDestroy")
    }

    override fun onPause() {
        super.onPause()
        LogCat.d(this.javaClass.simpleName + " onPause")
    }

    override fun onStart() {
        super.onStart()
        LogCat.d(this.javaClass.simpleName + " onStart")
    }

    override fun onResume() {
        super.onResume()
        LogCat.d(this.javaClass.simpleName + " onResume")
    }
}