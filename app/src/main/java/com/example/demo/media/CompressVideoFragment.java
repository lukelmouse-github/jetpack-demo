package com.example.demo.media;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.common.base.BaseFragment;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentCompressVideoBinding;
import com.therouter.router.Route;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Route(path = RouterPath.MEDIA, description = "compressVideo自定义参数调研")
public class CompressVideoFragment extends BaseFragment<FragmentCompressVideoBinding> {
    private static final String TAG = "CompressVideoFragment";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int REQUEST_MANAGE_STORAGE = 101;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String selectedVideoPath;
    private Uri selectedVideoUri;
    private VideoInfoExtractor.VideoInfo originalVideoInfo;
    private VideoInfoExtractor.VideoInfo compressedVideoInfo;
    private String compressedVideoPath;
    private Uri compressedVideoUri;
    private ExecutorService executorService;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 全屏播放相关变量
    private boolean isFullscreen = false;
    private int currentVideoPosition = 0;
    private boolean isOriginalVideo = true; // 标记当前播放的是原始视频还是压缩后的视频
    private MediaController fullscreenMediaController;

    // 用于选择视频的ActivityResultLauncher
    private final ActivityResultLauncher<Intent> selectVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedVideoUri = result.getData().getData();
                    if (selectedVideoUri != null) {
                        handleSelectedVideo(selectedVideoUri);
                    }
                }
            }
    );

    // 用于请求权限的ActivityResultLauncher
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    // 如果是Android 11及以上，还需要请求MANAGE_EXTERNAL_STORAGE权限
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                        requestManageExternalStoragePermission();
                    } else {
                        openVideoSelector();
                    }
                } else {
                    Toast.makeText(requireContext(), "需要存储权限才能选择视频", Toast.LENGTH_SHORT).show();
                }
            });

    // 用于请求MANAGE_EXTERNAL_STORAGE权限的ActivityResultLauncher
    private final ActivityResultLauncher<Intent> manageStorageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        openVideoSelector();
                    } else {
                        Toast.makeText(requireContext(), "需要所有文件访问权限才能选择视频", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public CompressVideoFragment() {
        super(R.layout.fragment_compress_video);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setupFullscreenControls();
    }

    @Override
    public void initView() {
        // 设置编码格式选择器
        String[] codecOptions = {"H.264 (AVC)", "H.265 (HEVC)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, codecOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCodec.setAdapter(adapter);
        binding.spinnerCodec.setSelection(0); // 默认选择H.264

        // 设置选择视频按钮点击事件
        binding.btnSelectVideo.setOnClickListener(v -> checkAndRequestPermissions());

        // 设置压缩按钮点击事件
        binding.btnCompress.setOnClickListener(v -> {
            if (selectedVideoPath != null) {
                compressVideo();
            } else {
                Toast.makeText(requireContext(), "请先选择视频", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置播放压缩后视频按钮点击事件
        binding.btnPlayCompressed.setOnClickListener(v -> {
            if (compressedVideoPath != null) {
                playCompressedVideo();
            } else {
                Toast.makeText(requireContext(), "请先压缩视频", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置MediaController
        MediaController mediaController = new MediaController(requireContext());
        binding.videoViewOriginal.setMediaController(mediaController);

        MediaController mediaController2 = new MediaController(requireContext());
        binding.videoViewCompressed.setMediaController(mediaController2);

        // 设置全屏按钮点击事件
        binding.btnFullscreenOriginal.setOnClickListener(v -> {
            isOriginalVideo = true;
            enterFullscreen();
        });

        binding.btnFullscreenCompressed.setOnClickListener(v -> {
            if (compressedVideoUri != null) {
                isOriginalVideo = false;
                enterFullscreen();
            } else {
                Toast.makeText(requireContext(), "请先压缩视频", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFullscreenControls() {
        // 设置全屏播放的MediaController
        fullscreenMediaController = new MediaController(requireContext());
        binding.videoViewFullscreen.setMediaController(fullscreenMediaController);

        // 设置退出全屏按钮点击事件
        binding.btnExitFullscreen.setOnClickListener(v -> exitFullscreen());

        // 设置视频播放完成监听器
        binding.videoViewFullscreen.setOnCompletionListener(mp -> {
            // 视频播放完成后，自动退出全屏
            if (isFullscreen) {
                exitFullscreen();
            }
        });
    }

    private void enterFullscreen() {
        if (isFullscreen) return;

        // 保存当前播放位置
        VideoView currentVideoView = isOriginalVideo ? binding.videoViewOriginal : binding.videoViewCompressed;
        currentVideoPosition = currentVideoView.getCurrentPosition();

        // 设置全屏视频源
        Uri videoUri = isOriginalVideo ? selectedVideoUri : compressedVideoUri;
        binding.videoViewFullscreen.setVideoURI(videoUri);

        // 显示全屏容器，隐藏滚动视图
        binding.scrollView.setVisibility(View.GONE);
        binding.fullscreenContainer.setVisibility(View.VISIBLE);

        // 开始播放并定位到之前的位置
        binding.videoViewFullscreen.seekTo(currentVideoPosition);
        binding.videoViewFullscreen.start();

        isFullscreen = true;

        // 隐藏系统UI（可选）
        hideSystemUI();
    }

    private void exitFullscreen() {
        if (!isFullscreen) return;

        // 保存当前播放位置
        currentVideoPosition = binding.videoViewFullscreen.getCurrentPosition();

        // 停止全屏播放
        binding.videoViewFullscreen.stopPlayback();

        // 隐藏全屏容器，显示滚动视图
        binding.fullscreenContainer.setVisibility(View.GONE);
        binding.scrollView.setVisibility(View.VISIBLE);

        // 恢复原来的视频播放位置
        VideoView targetVideoView = isOriginalVideo ? binding.videoViewOriginal : binding.videoViewCompressed;
        targetVideoView.seekTo(currentVideoPosition);

        isFullscreen = false;

        // 显示系统UI（可选）
        showSystemUI();
    }

    private void hideSystemUI() {
        if (getActivity() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void showSystemUI() {
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11及以上需要请求MANAGE_EXTERNAL_STORAGE权限
            if (Environment.isExternalStorageManager()) {
                openVideoSelector();
            } else {
                requestManageExternalStoragePermission();
            }
        } else {
            // Android 10及以下使用传统权限
            List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }

            if (permissionsToRequest.isEmpty()) {
                openVideoSelector();
            } else {
                requestPermissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
            }
        }
    }

    private void requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                manageStorageLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                manageStorageLauncher.launch(intent);
            }
        }
    }

    private void openVideoSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        selectVideoLauncher.launch(Intent.createChooser(intent, "选择视频"));
    }

    private void handleSelectedVideo(Uri uri) {
        // 保存选择的视频Uri
        selectedVideoUri = uri;
        // 获取视频文件路径
        selectedVideoPath = FileUtils.getPathFromUri(requireContext(), uri);
        if (selectedVideoPath == null) {
            Toast.makeText(requireContext(), "无法获取视频文件路径", Toast.LENGTH_SHORT).show();
            return;
        }

        // 在后台线程中获取视频信息
        executorService.execute(() -> {
            // 尝试从Uri获取视频信息
            try {
                originalVideoInfo = VideoInfoExtractor.getVideoInfo(requireContext(), uri);
            } catch (Exception e) {
                // 如果从Uri获取失败，尝试从文件路径获取
                originalVideoInfo = VideoInfoExtractor.getVideoInfo(selectedVideoPath);
            }

            mainHandler.post(() -> {
                // 更新UI
                binding.tvVideoInfo.setText("原始视频信息:\n" + originalVideoInfo.toString());

                // 设置默认参数
                binding.etBitrate.setText(String.valueOf(originalVideoInfo.getBitrate() / 1000)); // 转为Kbps
                binding.etFps.setText("60"); // 默认60fps
                binding.etWidth.setText(String.valueOf(originalVideoInfo.getWidth()));
                binding.etHeight.setText(String.valueOf(originalVideoInfo.getHeight()));

                // 播放原始视频
                playOriginalVideo();

                // 启用压缩按钮
                binding.btnCompress.setEnabled(true);
            });
        });
    }

    private void playOriginalVideo() {
        if (selectedVideoUri != null) {
            // 直接使用用户选择的Uri播放视频
            binding.videoViewOriginal.setVideoURI(selectedVideoUri);
            binding.videoViewOriginal.requestFocus();
            binding.videoViewOriginal.start();
        }
    }

    private void playCompressedVideo() {
        if (compressedVideoPath != null) {
            try {
                // 使用FileProvider创建一个内容Uri
                File compressedFile = new File(compressedVideoPath);
                compressedVideoUri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().getPackageName() + ".provider",
                        compressedFile);
                // 使用内容Uri播放视频
                binding.videoViewCompressed.setVideoURI(compressedVideoUri);
                binding.videoViewCompressed.requestFocus();
                binding.videoViewCompressed.start();
            } catch (Exception e) {
                Log.e(TAG, "播放压缩视频失败: " + e.getMessage(), e);
                Toast.makeText(requireContext(), "播放压缩视频失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void compressVideo() {
        // 获取压缩参数
        int bitrateKbps;
        int fps;
        int width;
        int height;
        String codecName;

        try {
            bitrateKbps = Integer.parseInt(binding.etBitrate.getText().toString());
            fps = Integer.parseInt(binding.etFps.getText().toString());
            width = Integer.parseInt(binding.etWidth.getText().toString());
            height = Integer.parseInt(binding.etHeight.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "请输入有效的数字参数", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取编码格式
        codecName = binding.spinnerCodec.getSelectedItemPosition() == 0 ?
                VideoCompressor.CODEC_H264 : VideoCompressor.CODEC_H265;

        // 生成输出文件路径 - 使用应用私有目录
        File outputDir = new File(requireContext().getFilesDir(), "compressed_videos");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 获取编码格式的显示名称
        String codecDisplayName = binding.spinnerCodec.getSelectedItemPosition() == 0 ? "h264" : "h265";

        String fileName = "compressed_" + System.currentTimeMillis() + "_" +
                bitrateKbps + "kbps_" + fps + "fps_" + width + "x" + height + "_" +
                codecDisplayName + ".mp4";
        String tempPath = new File(outputDir, fileName).getAbsolutePath();

        // 显示进度对话框
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("压缩中");
        progressDialog.setMessage("正在压缩视频，请稍候...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // 记录开始时间
        final long startTime = System.currentTimeMillis();

        // 在后台线程中压缩视频
        final int finalBitrateKbps = bitrateKbps;
        final int finalFps = fps;
        final int finalWidth = width;
        final int finalHeight = height;
        final String finalCodecName = codecName;
        final String finalTempPath = tempPath;

        executorService.execute(() -> {
            try {
                // 创建VideoCompressor实例并压缩视频
                VideoCompressor compressor = new VideoCompressor();
                compressedVideoPath = compressor.compressVideo(
                        selectedVideoPath,
                        finalBitrateKbps,
                        finalFps,
                        finalCodecName,
                        finalWidth,
                        finalHeight,
                        finalTempPath
                );

                // 计算压缩时间
                final long endTime = System.currentTimeMillis();
                final long compressionTime = endTime - startTime;

                // 获取压缩后视频信息
                compressedVideoInfo = VideoInfoExtractor.getVideoInfo(compressedVideoPath);

                // 在主线程中更新UI
                mainHandler.post(() -> {
                    progressDialog.dismiss();

                    // 更新压缩结果信息，显示对比数据
                    SpannableStringBuilder compressionInfo = new SpannableStringBuilder();

                    // 添加压缩时间
                    compressionInfo.append("压缩时间: ").append(String.format("%.2f", compressionTime / 1000.0)).append(" 秒\n\n");

                    // 添加标题
                    SpannableString title = new SpannableString("压缩前后对比:\n");
                    title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    compressionInfo.append(title);

                    // 分辨率对比
                    compressionInfo.append("分辨率: ");
                    compressionInfo.append(originalVideoInfo.getWidth() + "x" + originalVideoInfo.getHeight());
                    compressionInfo.append(" → ");
                    compressionInfo.append(compressedVideoInfo.getWidth() + "x" + compressedVideoInfo.getHeight());
                    if (originalVideoInfo.getWidth() != compressedVideoInfo.getWidth() ||
                        originalVideoInfo.getHeight() != compressedVideoInfo.getHeight()) {
                        SpannableString changed = new SpannableString(" (已变化)");
                        changed.setSpan(new ForegroundColorSpan(Color.RED), 0, changed.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        compressionInfo.append(changed);
                    }
                    compressionInfo.append("\n");

                    // 比特率对比
                    int originalBitrateKbps = originalVideoInfo.getBitrate() / 1000;
                    int compressedBitrateKbps = compressedVideoInfo.getBitrate() / 1000;
                    float bitrateReductionPercent = 100f * (1 - (float)compressedBitrateKbps / originalBitrateKbps);

                    compressionInfo.append("比特率: ");
                    compressionInfo.append(originalBitrateKbps + " Kbps");
                    compressionInfo.append(" → ");
                    compressionInfo.append(compressedBitrateKbps + " Kbps");
                    if (bitrateReductionPercent > 0) {
                        SpannableString reduced = new SpannableString(String.format(" (减少 %.1f%%)", bitrateReductionPercent));
                        reduced.setSpan(new ForegroundColorSpan(Color.GREEN), 0, reduced.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        compressionInfo.append(reduced);
                    }
                    compressionInfo.append("\n");

                    // 帧率对比
                    compressionInfo.append("帧率: ");
                    compressionInfo.append(originalVideoInfo.getFrameRate() + " fps");
                    compressionInfo.append(" → ");
                    compressionInfo.append(compressedVideoInfo.getFrameRate() + " fps");
                    if (originalVideoInfo.getFrameRate() != compressedVideoInfo.getFrameRate()) {
                        SpannableString changed = new SpannableString(" (已变化)");
                        changed.setSpan(new ForegroundColorSpan(Color.BLUE), 0, changed.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        compressionInfo.append(changed);
                    }
                    compressionInfo.append("\n");

                    // 编码格式对比
                    compressionInfo.append("编码: ");
                    compressionInfo.append(originalVideoInfo.getMimeType());
                    compressionInfo.append(" → ");
                    compressionInfo.append(compressedVideoInfo.getMimeType());
                    if (!originalVideoInfo.getMimeType().equals(compressedVideoInfo.getMimeType())) {
                        SpannableString changed = new SpannableString(" (已变化)");
                        changed.setSpan(new ForegroundColorSpan(Color.BLUE), 0, changed.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        compressionInfo.append(changed);
                    }
                    compressionInfo.append("\n");

                    // 文件大小对比
                    float originalSizeMB = originalVideoInfo.getFileSize() / (1024f * 1024f);
                    float compressedSizeMB = compressedVideoInfo.getFileSize() / (1024f * 1024f);
                    float sizeReductionMB = originalSizeMB - compressedSizeMB;
                    float sizeReductionPercent = 100f * (1 - compressedSizeMB / originalSizeMB);

                    compressionInfo.append("文件大小: ");
                    compressionInfo.append(String.format("%.2f MB", originalSizeMB));
                    compressionInfo.append(" → ");
                    compressionInfo.append(String.format("%.2f MB", compressedSizeMB));
                    if (sizeReductionMB > 0) {
                        SpannableString reduced = new SpannableString(String.format(" (减少 %.2f MB, %.1f%%)", sizeReductionMB, sizeReductionPercent));
                        reduced.setSpan(new ForegroundColorSpan(Color.GREEN), 0, reduced.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        compressionInfo.append(reduced);
                    }
                    compressionInfo.append("\n");

                    // 压缩比
                    float compressionRatio = (float) originalVideoInfo.getFileSize() / compressedVideoInfo.getFileSize();
                    compressionInfo.append("\n压缩比: ").append(String.format("%.2f", compressionRatio)).append("x");
                    binding.tvCompressInfo.setText(compressionInfo);

                    // 启用播放压缩后视频按钮
                    binding.btnPlayCompressed.setEnabled(true);

                    // 自动播放压缩后的视频
                    playCompressedVideo();

                    Toast.makeText(requireContext(), "视频压缩完成", Toast.LENGTH_SHORT).show();
                });
            } catch (IOException e) {
                Log.e(TAG, "压缩视频失败: " + e.getMessage(), e);

                // 在主线程中更新UI
                mainHandler.post(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "压缩视频失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 处理屏幕旋转事件
        if (isFullscreen) {
            // 如果是全屏状态，调整全屏视图的布局
            adjustFullscreenLayout();
        }
    }

    private void adjustFullscreenLayout() {
        // 根据屏幕方向调整全屏视图的布局
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏时，视频占满整个屏幕
            ViewGroup.LayoutParams params = binding.videoViewFullscreen.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            binding.videoViewFullscreen.setLayoutParams(params);
        } else {
            // 竖屏时，保持视频的宽高比
            ViewGroup.LayoutParams params = binding.videoViewFullscreen.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            binding.videoViewFullscreen.setLayoutParams(params);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 暂停所有视频播放
        if (binding.videoViewOriginal.isPlaying()) {
            binding.videoViewOriginal.pause();
        }
        if (binding.videoViewCompressed.isPlaying()) {
            binding.videoViewCompressed.pause();
        }
        if (binding.videoViewFullscreen.isPlaying()) {
            binding.videoViewFullscreen.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 如果是全屏状态，恢复全屏视频播放
        if (isFullscreen && !binding.videoViewFullscreen.isPlaying()) {
            binding.videoViewFullscreen.start();
        }
    }

    @Override
    public boolean onBackPressed() {
        // 处理返回键事件
        if (isFullscreen) {
            exitFullscreen();
            return true; // 返回true表示已处理返回事件
        } else {
            return super.onBackPressed(); // 调用父类方法并返回其结果
        }
    }
}