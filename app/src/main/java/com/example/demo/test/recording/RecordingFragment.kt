package com.example.demo.test.recording

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import com.drake.tooltip.toast
import com.example.common.base.BaseFragment
import com.example.common.log.ALog
import com.example.common.routers.RouterPath
import com.example.demo.R
import com.example.demo.databinding.FragmentRecordingTestMainBinding
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.therouter.router.Route
import java.io.File

@Route(path = RouterPath.TEST_RECORDING, description = "测试后台录音功能")
class RecordingFragment : BaseFragment<FragmentRecordingTestMainBinding>(R.layout.fragment_recording_test_main) {
    private var mediaPlayer: MediaPlayer? = null
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    override fun initView() {
        super.initView()
    }

    override fun initData() {
        super.initData()

        requestPermissions()

        binding.btnStart.setOnClickListener {
            startRecordingService()
        }
        binding.btnEnd.setOnClickListener {
            stopRecordingService()
        }

        binding.btnPlay.setOnClickListener {
            playRecording()
        }
    }

    private fun requestPermissions() {
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
    }

    private fun startRecordingService() {
        if (XXPermissions.isGranted(requireContext(), Permission.RECORD_AUDIO)) {
            val intent = Intent(requireContext(), RecordingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(intent)
            } else {
                requireContext().startService(intent)
            }
            toast("录音服务已启动")
        } else {
            toast("请先授予录音权限")
            requestPermissions()
        }
    }

    private fun stopRecordingService() {
        val intent = Intent(requireContext(), RecordingService::class.java)
        requireContext().stopService(intent)
        toast("录音服务已停止")
    }

    private fun playRecording() {
        val file = File(requireContext().getExternalFilesDir(null), "recording.wav")
        if (!file.exists()) {
            toast("录音文件不存在")
            return
        }

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.path)
                prepare()
                start()
            }
            toast("正在播放录音")
        } catch (e: Exception) {
            ALog.e("播放录音失败: ${e.message}")
            toast("播放录音失败: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}