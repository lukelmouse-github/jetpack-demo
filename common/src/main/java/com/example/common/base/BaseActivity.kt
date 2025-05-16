package com.example.common.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.drake.engine.base.EngineActivity
import com.example.common.log.ALog
import com.therouter.TheRouter

abstract class BaseActivity<B : ViewDataBinding>(@LayoutRes contentLayoutId: Int = 0) : EngineActivity<B>(contentLayoutId) {

    override fun initView() {
        ALog.d("initView ${this.javaClass.simpleName}")
    }

    override fun initData() {
        ALog.d("initData ${this.javaClass.simpleName}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TheRouter.inject(this) // 必须放这里，哪些注解才有用。！要比Engine执行的时机早。
        super.onCreate(savedInstanceState)
        ALog.d("onCreate ${this.javaClass.simpleName}")
    }

    override fun onStop() {
        super.onStop()
        ALog.d("onStop ${this.javaClass.simpleName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        ALog.d("onDestroy ${this.javaClass.simpleName}")
    }

    override fun onPause() {
        super.onPause()
        ALog.d("onPause ${this.javaClass.simpleName}")
    }

    override fun onStart() {
        super.onStart()
        ALog.d("onStart ${this.javaClass.simpleName}")
    }

    override fun onResume() {
        super.onResume()
        ALog.d("onResume ${this.javaClass.simpleName}")
    }

    override fun onRestart() {
        super.onRestart()
        ALog.d("onRestart ${this.javaClass.simpleName}")
    }

}