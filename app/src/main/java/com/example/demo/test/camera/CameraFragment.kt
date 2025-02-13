package com.example.demo.test.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.common.base.BaseFragment
import com.example.common.routers.RouterPath
import com.example.demo.R
import com.example.demo.databinding.FragmentCameraBinding
import com.therouter.router.Route
import java.io.File
import java.io.IOException
import kotlin.math.abs

@Route(path = RouterPath.TEST_CAMERA, description = "相机测试")
class CameraFragment : BaseFragment<FragmentCameraBinding>(R.layout.fragment_camera), SurfaceHolder.Callback {
    private var camera: Camera? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var outputFile: File? = null
    private val handler = Handler(Looper.getMainLooper())
    private var camParams : Camera.Parameters? = null

    override fun initView() {
        if (!checkCameraHardware()) {
            showError("设备不支持相机功能")
            activity?.finish()
            return
        }

        if (checkCameraPermission()) {
            initializeCamera()
        } else {
            requestCameraPermission()
        }

        binding.apply {
            cameraPreview.holder.addCallback(this@CameraFragment)
            btnStartRecording.setOnClickListener { startRecording() }
            btnPauseRecording.setOnClickListener { pauseRecording() }
            btnSetZoom.setOnClickListener { setMinZoom() }
            btnPreviewVideo.setOnClickListener { previewVideo() }
        }
        updateUIState()

    }

    private fun checkCameraHardware(): Boolean {
        return activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true
    }

    private fun checkCameraPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val audioPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // Android 10及以上版本不需要WRITE_EXTERNAL_STORAGE权限
        } else {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        return cameraPermission && audioPermission && storagePermission
    }

    private fun requestCameraPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        } else {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(requireActivity(), permissions, CAMERA_PERMISSION_REQUEST_CODE)
    }

    private fun initializeCamera() {
        try {
            camera = Camera.open(0)
            camParams = camera?.parameters
            setCameraDisplayOrientation()
            camera?.setPreviewDisplay(binding.cameraPreview.holder)
            camera?.startPreview()
            setOptimalPreviewSize()
            updateUIState()
        } catch (e: IOException) {
            Log.e(TAG, "设置相机预览失败", e)
            showError("设置相机预览失败: ${e.message}")
        } catch (e: RuntimeException) {
            Log.e(TAG, "打开相机失败", e)
            showError("打开相机失败: ${e.message}")
        }
    }

    private fun setCameraDisplayOrientation() {
        val rotation = activity?.windowManager?.defaultDisplay?.rotation ?: 0
        val degrees = when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo)
        val result = (cameraInfo.orientation - degrees + 360) % 360
        camera?.setDisplayOrientation(result)
    }

    private fun setOptimalPreviewSize() {
        val parameters = camParams ?: return
        val sizes = parameters.supportedPreviewSizes
        val targetRatio = binding.cameraPreview.width.toDouble() / binding.cameraPreview.height
        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE

        sizes?.forEach { size ->
            val ratio = size.width.toDouble() / size.height
            if (abs(ratio - targetRatio) < minDiff) {
                optimalSize = size
                minDiff = abs(ratio - targetRatio)
            }
        }

        optimalSize?.let {
            parameters.setPreviewSize(it.width, it.height)
            camParams = parameters
            camera?.parameters = camParams
        }
    }

    private fun startRecording() {
        if (isRecording) {
            showError("已经在录制中")
            return
        }

        if (!checkCameraPermission()) {
            requestCameraPermission()
            return
        }

        try {
            releaseMediaRecorder()
            camera?.stopPreview()
            camera?.unlock()

            mediaRecorder = MediaRecorder().apply {
                setCamera(camera)
                setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)

                val profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
                setProfile(profile)

                val optimalSize = getOptimalVideoSize()
                setVideoSize(optimalSize.width, optimalSize.height)

                setOrientationHint(90)

                val moviesDir = File(requireContext().getExternalFilesDir(null), "Movies")
                if (!moviesDir.exists()) {
                    moviesDir.mkdirs()
                }
                outputFile = File(moviesDir, "video_${System.currentTimeMillis()}.mp4")
                setOutputFile(outputFile?.path)

                prepare()
            }

            mediaRecorder?.start()
            isRecording = true
            updateUIState()
            showMessage("开始录制")

            handler.postDelayed({
                setMaxZoom()
            }, 1000)

            handler.postDelayed({
                pauseRecording()
            }, 5000)
        } catch (e: Exception) {
            Log.e(TAG, "录制失败", e)
            showError("录制失败: ${e.message}")
            releaseMediaRecorder()
        }
    }

    private fun getOptimalVideoSize(): Camera.Size {
        if (camParams == null) {
            Log.e(TAG, "Camera parameters are null")
            throw IllegalStateException("Camera parameters are null")
        } else {
            val sizes = camParams?.supportedVideoSizes ?: camParams?.supportedPreviewSizes
            return sizes?.maxByOrNull { it.width * it.height }
                ?: throw IllegalStateException("No supported video sizes found")
        }

    }

    private fun pauseRecording() {
        if (!isRecording) {
            showError("没有正在进行的录制")
            return
        }

        try {
            mediaRecorder?.stop()
            releaseMediaRecorder()
            camera?.lock()
            camera?.startPreview()
            isRecording = false
            updateUIState()
            showMessage("录制已停止")
        } catch (e: Exception) {
            Log.e(TAG, "停止录制失败", e)
            showError("停止录制失败: ${e.message}")
        }
    }

    private fun setMinZoom() {
        camParams?.let { params ->
            if (params.isZoomSupported) {
                params.zoom = 0
                try {
                    camParams = params
                    camera?.parameters = camParams
                    Log.d(TAG, "已设置最大缩放")
                } catch (e: Exception) {
                    Log.e(TAG, "设置缩放失败", e)
                }
            } else {
                Log.w(TAG, "相机不支持缩放")
            }
        }
    }

    private fun setMaxZoom() {
        camParams?.let { params ->
            if (params.isZoomSupported) {
                params.zoom = params.maxZoom
                try {
                    camParams = params
                    camera?.parameters = camParams
                    Log.d(TAG, "已设置最大缩放")
                } catch (e: Exception) {
                    Log.e(TAG, "设置缩放失败", e)
                }
            } else {
                Log.w(TAG, "相机不支持缩放")
            }
        }
    }

    private fun previewVideo() {
        outputFile?.let { file ->
            if (!file.exists()) {
                showError("视频文件不存在")
                return
            }
            try {
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "video/mp4")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "无法预览视频", e)
                showError("无法预览视频: ${e.message}")
            }
        } ?: showError("没有可预览的视频")
    }

    private fun updateUIState() {
        binding.apply {
            btnStartRecording.isEnabled = !isRecording && camera != null
            btnPauseRecording.isEnabled = isRecording
            btnSetZoom.isEnabled = camParams?.isZoomSupported == true
            btnPreviewVideo.isEnabled = outputFile != null
            statusText.text = if (isRecording) "正在录制..." else "准备就绪"
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            camera?.setPreviewDisplay(holder)
            camera?.startPreview()
        } catch (e: IOException) {
            Log.e(TAG, "设置相机预览失败", e)
            showError("设置相机预览失败: ${e.message}")
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (holder.surface == null) return

        try {
            camera?.stopPreview()
        } catch (e: Exception) {
            // 忽略尝试停止不存在的预览时的异常
        }

        try {
            camera?.setPreviewDisplay(holder)
            camera?.startPreview()
        } catch (e: Exception) {
            Log.e(TAG, "启动相机预览失败", e)
            showError("启动相机预览失败: ${e.message}")
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // 相机预览将在这里自动停止，所以不需要额外操作
    }

    override fun onPause() {
        super.onPause()
        releaseMediaRecorder()
        releaseCamera()
    }

    override fun onResume() {
        super.onResume()
        if (camera == null) {
            initializeCamera()
        }
    }

    private fun releaseCamera() {
        try {
            camera?.stopPreview()
            camera?.release()
            camera = null
        } catch (e: Exception) {
            Log.e(TAG, "释放相机失败", e)
        }
    }

    private fun releaseMediaRecorder() {
        try {
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null
            camera?.lock()
        } catch (e: Exception) {
            Log.e(TAG, "释放 MediaRecorder 失败", e)
        }
    }

    private fun showError(message: String) {
        Log.e(TAG, message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val TAG = "CameraFragment"
    }
}
