package com.example.demo.study.view

import com.example.common.base.BaseFragment
import com.example.common.routers.RouterPath
import com.example.demo.R
import com.example.demo.databinding.FragmentStudyLayoutBinding
import com.therouter.router.Route

@Route(path = RouterPath.STUDY_MAIN_LAYOUT, description = "布局")
class LayoutFragment : BaseFragment<FragmentStudyLayoutBinding>(R.layout.fragment_study_layout) {
    override fun initView() {
        super.initView()

    }

    override fun initData() {
        super.initData()
    }
}