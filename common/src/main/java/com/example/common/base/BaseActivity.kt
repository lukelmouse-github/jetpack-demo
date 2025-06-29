package com.example.common.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.drake.engine.base.EngineActivity
import com.example.common.log.ALog
import com.therouter.TheRouter

abstract class BaseActivity<B : ViewDataBinding>(@LayoutRes contentLayoutId: Int = 0) : EngineActivity<B>(contentLayoutId) {

    // 控制是否打印生命周期日志
    protected open fun enableLifecycleLog(): Boolean = false

    override fun initView() {
        if (enableLifecycleLog()) {
            ALog.dd("initView ${this.javaClass.simpleName}")
        }
    }

    override fun initData() {
        if (enableLifecycleLog()) {
            ALog.dd("initData ${this.javaClass.simpleName}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TheRouter.inject(this)
        super.onCreate(savedInstanceState)
        if (enableLifecycleLog()) {
            ALog.dd("onCreate ${this.javaClass.simpleName}")
        }
    }

    override fun onStop() {
        super.onStop()
        if (enableLifecycleLog()) {
            ALog.dd("onStop ${this.javaClass.simpleName}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (enableLifecycleLog()) {
            ALog.dd("onDestroy ${this.javaClass.simpleName}")
        }
    }

    override fun onPause() {
        super.onPause()
        if (enableLifecycleLog()) {
            ALog.dd("onPause ${this.javaClass.simpleName}")
        }
    }

    override fun onStart() {
        super.onStart()
        if (enableLifecycleLog()) {
            ALog.dd("onStart ${this.javaClass.simpleName}")
        }
    }

    override fun onResume() {
        super.onResume()
        if (enableLifecycleLog()) {
            ALog.dd("onResume ${this.javaClass.simpleName}")
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (enableLifecycleLog()) {
            ALog.dd("onRestart ${this.javaClass.simpleName}")
        }
    }

}