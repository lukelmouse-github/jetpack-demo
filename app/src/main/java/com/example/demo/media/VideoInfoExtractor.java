package com.example.demo.media;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

/**
 * 视频信息提取工具类
 */
public class VideoInfoExtractor {
    private static final String TAG = "VideoInfoExtractor";

    /**
     * 视频信息类
     */
    public static class VideoInfo {
        private int width;
        private int height;
        private int bitrate; // 比特率，单位：bps
        private float frameRate; // 帧率
        private String mimeType; // 编码类型
        private long durationMs; // 时长，单位：毫秒
        private String filePath; // 文件路径
        private long fileSize; // 文件大小，单位：字节

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getBitrate() {
            return bitrate;
        }

        public void setBitrate(int bitrate) {
            this.bitrate = bitrate;
        }

        public float getFrameRate() {
            return frameRate;
        }

        public void setFrameRate(float frameRate) {
            this.frameRate = frameRate;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public long getDurationMs() {
            return durationMs;
        }

        public void setDurationMs(long durationMs) {
            this.durationMs = durationMs;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        /**
         * 获取编码格式的友好名称
         * @return 编码格式的友好名称
         */
        private String getCodecDisplayName() {
            if (mimeType == null) {
                return "未知";
            }

            switch (mimeType) {
                case "video/avc":
                    return "H.264 (AVC)";
                case "video/hevc":
                    return "H.265 (HEVC)";
                case "video/mp4v-es":
                    return "MPEG-4";
                case "video/3gpp":
                    return "H.263";
                case "video/x-vnd.on2.vp8":
                    return "VP8";
                case "video/x-vnd.on2.vp9":
                    return "VP9";
                case "video/av01":
                    return "AV1";
                default:
                    return mimeType;
            }
        }

        @NonNull
        @Override
        public String toString() {
            return "分辨率: " + width + "x" + height + "\n" +
                    "比特率: " + (bitrate / 1000) + " Kbps\n" +
                    "帧率: " + frameRate + " fps\n" +
                    "编码: " + getCodecDisplayName() + " (" + mimeType + ")\n" +
                    "时长: " + (durationMs / 1000) + " 秒\n" +
                    "文件大小: " + (fileSize / 1024 / 1024) + " MB";
        }
    }

    /**
     * 获取视频信息
     *
     * @param filePath 视频文件路径
     * @return 视频信息
     */
    public static VideoInfo getVideoInfo(String filePath) throws IOException {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setFilePath(filePath);

        MediaExtractor extractor = null;
        MediaMetadataRetriever retriever = null;

        try {
            // 使用MediaExtractor获取视频轨道信息
            extractor = new MediaExtractor();
            extractor.setDataSource(filePath);

            // 查找视频轨道
            int videoTrackIndex = -1;
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime != null && mime.startsWith("video/")) {
                    videoTrackIndex = i;
                    break;
                }
            }

            if (videoTrackIndex >= 0) {
                MediaFormat format = extractor.getTrackFormat(videoTrackIndex);
                // 获取视频宽高
                if (format.containsKey(MediaFormat.KEY_WIDTH)) {
                    videoInfo.setWidth(format.getInteger(MediaFormat.KEY_WIDTH));
                }
                if (format.containsKey(MediaFormat.KEY_HEIGHT)) {
                    videoInfo.setHeight(format.getInteger(MediaFormat.KEY_HEIGHT));
                }
                // 获取比特率
                if (format.containsKey(MediaFormat.KEY_BIT_RATE)) {
                    videoInfo.setBitrate(format.getInteger(MediaFormat.KEY_BIT_RATE));
                }
                // 获取帧率
                if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                    videoInfo.setFrameRate(format.getInteger(MediaFormat.KEY_FRAME_RATE));
                }
                // 获取编码类型
                if (format.containsKey(MediaFormat.KEY_MIME)) {
                    videoInfo.setMimeType(format.getString(MediaFormat.KEY_MIME));
                }
                // 获取时长
                if (format.containsKey(MediaFormat.KEY_DURATION)) {
                    videoInfo.setDurationMs(format.getLong(MediaFormat.KEY_DURATION) / 1000); // 微秒转毫秒
                }
            }

            // 使用MediaMetadataRetriever获取更多信息
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(filePath);
            // 获取时长（如果MediaExtractor没有获取到）
            if (videoInfo.getDurationMs() == 0) {
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (duration != null) {
                    videoInfo.setDurationMs(Long.parseLong(duration));
                }
            }
            // 获取比特率（如果MediaExtractor没有获取到）
            if (videoInfo.getBitrate() == 0) {
                String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                if (bitrate != null) {
                    videoInfo.setBitrate(Integer.parseInt(bitrate));
                }
            }
            // 获取文件大小
            java.io.File file = new java.io.File(filePath);
            if (file.exists()) {
                videoInfo.setFileSize(file.length());
            }

            return videoInfo;
        } catch (IOException e) {
            Log.e(TAG, "Error extracting video info: " + e.getMessage(), e);
            return videoInfo;
        } finally {
            if (extractor != null) {
                extractor.release();
            }
            if (retriever != null) {
                retriever.release();
            }
        }
    }

    /**
     * 获取视频信息（从Uri）
     *
     * @param context 上下文
     * @param uri     视频Uri
     * @return 视频信息
     */
    public static VideoInfo getVideoInfo(Context context, Uri uri) throws IOException {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setFilePath(uri.toString());

        MediaExtractor extractor = null;
        MediaMetadataRetriever retriever = null;

        try {
            // 使用MediaExtractor获取视频轨道信息
            extractor = new MediaExtractor();
            extractor.setDataSource(context, uri, null);

            // 查找视频轨道
            int videoTrackIndex = -1;
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime != null && mime.startsWith("video/")) {
                    videoTrackIndex = i;
                    break;
                }
            }

            if (videoTrackIndex >= 0) {
                MediaFormat format = extractor.getTrackFormat(videoTrackIndex);
                // 获取视频宽高
                if (format.containsKey(MediaFormat.KEY_WIDTH)) {
                    videoInfo.setWidth(format.getInteger(MediaFormat.KEY_WIDTH));
                }
                if (format.containsKey(MediaFormat.KEY_HEIGHT)) {
                    videoInfo.setHeight(format.getInteger(MediaFormat.KEY_HEIGHT));
                }
                // 获取比特率
                if (format.containsKey(MediaFormat.KEY_BIT_RATE)) {
                    videoInfo.setBitrate(format.getInteger(MediaFormat.KEY_BIT_RATE));
                }
                // 获取帧率
                if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                    videoInfo.setFrameRate(format.getInteger(MediaFormat.KEY_FRAME_RATE));
                }
                // 获取编码类型
                if (format.containsKey(MediaFormat.KEY_MIME)) {
                    videoInfo.setMimeType(format.getString(MediaFormat.KEY_MIME));
                }
                // 获取时长
                if (format.containsKey(MediaFormat.KEY_DURATION)) {
                    videoInfo.setDurationMs(format.getLong(MediaFormat.KEY_DURATION) / 1000); // 微秒转毫秒
                }
            }

            // 使用MediaMetadataRetriever获取更多信息
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, uri);
            // 获取时长（如果MediaExtractor没有获取到）
            if (videoInfo.getDurationMs() == 0) {
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (duration != null) {
                    videoInfo.setDurationMs(Long.parseLong(duration));
                }
            }
            // 获取比特率（如果MediaExtractor没有获取到）
            if (videoInfo.getBitrate() == 0) {
                String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                if (bitrate != null) {
                    videoInfo.setBitrate(Integer.parseInt(bitrate));
                }
            }

            // 尝试获取文件大小
            try {
                java.io.InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    videoInfo.setFileSize(inputStream.available());
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error getting file size: " + e.getMessage(), e);
            }

            return videoInfo;
        } catch (IOException e) {
            Log.e(TAG, "Error extracting video info: " + e.getMessage(), e);
            return videoInfo;
        } finally {
            if (extractor != null) {
                extractor.release();
            }
            if (retriever != null) {
                retriever.release();
            }
        }
    }
}