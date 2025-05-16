package com.example.demo.media;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件工具类
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 从Uri获取文件路径
     *
     * @param context 上下文
     * @param uri     Uri
     * @return 文件路径
     */
    @Nullable
    public static String getPathFromUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        // 检查是否是文件Uri
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        // 检查是否是内容Uri
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 检查是否是媒体文件
            String path = getPathFromMediaUri(context, uri);
            if (!TextUtils.isEmpty(path)) {
                return path;
            }

            // 检查是否是文档文件
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                path = getPathFromDocumentUri(context, uri);
                if (!TextUtils.isEmpty(path)) {
                    return path;
                }
            }

            // 如果无法获取路径，则复制文件到缓存目录
            return copyUriToCache(context, uri);
        }

        return null;
    }

    /**
     * 从媒体Uri获取文件路径
     *
     * @param context 上下文
     * @param uri     Uri
     * @return 文件路径
     */
    @Nullable
    private static String getPathFromMediaUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting path from media URI: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 从文档Uri获取文件路径
     *
     * @param context 上下文
     * @param uri     Uri
     * @return 文件路径
     */
    @Nullable
    private static String getPathFromDocumentUri(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.substring(4);
                    }
                    try {
                        Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing document ID: " + e.getMessage(), e);
                    }
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        return null;
    }

    /**
     * 获取数据列
     *
     * @param context       上下文
     * @param uri           Uri
     * @param selection     选择条件
     * @param selectionArgs 选择参数
     * @return 数据列
     */
    @Nullable
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting data column: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 复制Uri到缓存目录
     *
     * @param context 上下文
     * @param uri     Uri
     * @return 文件路径
     */
    @Nullable
    private static String copyUriToCache(Context context, Uri uri) {
        String fileName = getFileName(context, uri);
        if (TextUtils.isEmpty(fileName)) {
            fileName = "temp_" + System.currentTimeMillis();
        }

        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, fileName);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return null;
            }
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024]; // 4KB buffer
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error copying URI to cache: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取文件名
     *
     * @param context 上下文
     * @param uri     Uri
     * @return 文件名
     */
    @NonNull
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex >= 0) {
                        result = cursor.getString(columnIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name: " + e.getMessage(), e);
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * 生成压缩后的文件路径
     *
     * @param originalPath 原始文件路径
     * @param bitrateKbps  比特率
     * @param fps          帧率
     * @param width        宽度
     * @param height       高度
     * @param codecName    编码格式
     * @return 压缩后的文件路径
     */
    @NonNull
    public static String generateCompressedFilePath(String originalPath, int bitrateKbps, int fps, int width, int height, String codecName) {
        String fileName = originalPath.substring(originalPath.lastIndexOf('/') + 1);
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
        String codecSuffix = codecName.equals(VideoCompressor.CODEC_H264) ? "h264" : "h265";
        
        String compressedFileName = nameWithoutExt + "_compressed_" + bitrateKbps + "kbps_" + fps + "fps_" + 
                width + "x" + height + "_" + codecSuffix + extension + (System.currentTimeMillis() % 10000);
        
        return originalPath.substring(0, originalPath.lastIndexOf('/') + 1) + compressedFileName;
    }

    /**
     * 是否是外部存储文档
     *
     * @param uri Uri
     * @return 是否是外部存储文档
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * 是否是下载文档
     *
     * @param uri Uri
     * @return 是否是下载文档
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * 是否是媒体文档
     *
     * @param uri Uri
     * @return 是否是媒体文档
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}