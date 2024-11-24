package com.example.demo.study

import com.example.common.base.BaseFragment
import com.example.common.routers.Router
import com.example.common.routers.RouterPath
import com.example.demo.R
import com.example.demo.databinding.FragmentStudyMainBinding
import com.therouter.router.Route

@Route(path = RouterPath.STUDY_MAIN, description = "学习主页")
class StudyMainFragment : BaseFragment<FragmentStudyMainBinding>(R.layout.fragment_study_main) {
    override fun initView() {
        super.initView()
    }

    override fun initData() {
        super.initData()
        binding.btn10.setOnClickListener {
            Router.openFragment(RouterPath.STUDY_MAIN_DRAWING);
        }
    }
}