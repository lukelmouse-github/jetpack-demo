package com.example.core.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


abstract class BaseFragment<T : BaseViewModel, M : ViewDataBinding> : Fragment() {

    abstract val mViewModel: T
    lateinit var mViewBinding: M

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        return mViewBinding.root
    }

    abstract fun getLayoutResId(): Int
    abstract fun initData()
    abstract fun initView()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 直接使用 mViewModel，不需要任何初始化代码！
        initData()
        initView()
    }
}