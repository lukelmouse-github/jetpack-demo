package com.example.demo.test.recording

import com.drake.logcat.LogCat
import com.drake.tooltip.toast
import com.example.common.base.BaseFragment
import com.example.common.routers.RouterPath
import com.example.demo.R
import com.example.demo.databinding.FragmentRecordingTestMainBinding
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.therouter.router.Route


@Route(path = RouterPath.TEST_RECORDING, description = "测试后台录音功能")
class RecordingFragment : BaseFragment<FragmentRecordingTestMainBinding>(R.layout.fragment_recording_test_main) {
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    override fun initView() {
        super.initView()
    }

    override fun initData() {
        super.initData()

        XXPermissions.with(requireContext())
            // 申请单个权限
            .permission(Permission.RECORD_AUDIO)
            // 申请多个权限
            .permission(Permission.Group.CALENDAR)
            // 设置权限请求拦截器（局部设置）
            //.interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            //.unchecked()
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (!allGranted) {
                        toast("获取部分权限成功，但部分权限未正常授予")
                        return
                    }
                    toast("获取录音和日历权限成功")
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        toast("被永久拒绝授权，请手动授予录音和日历权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(requireContext(), permissions)
                    } else {
                        toast("获取录音和日历权限失败")
                    }
                }
            })

//        binding.
//        binding.btn10.setOnClickListener {
//            Router.openFragment(RouterPath.STUDY_MAIN_DRAWING);
//        }
    }
}