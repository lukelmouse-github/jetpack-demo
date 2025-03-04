package com.example.common.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SysUtils {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 判断给定的类是否是 Kotlin 类
     *
     * @param clazz 要检查的类
     * @return 如果是 Kotlin 类返回 true，否则返回 false
     */
    public static boolean isKt(Class<?> clazz) {
        // 检查类名是否以 "kotlin" 开头
        return clazz.getName().startsWith("kotlin");
    }

    /**
     * 在后台线程池中执行任务
     *
     * @param runnable 要执行的任务
     */
    public static void runOnBackground(Runnable runnable) {
        executorService.execute(runnable);
    }

    /**
     * 在后台线程池中执行任务，并在主线程中处理结果
     *
     * @param task 要执行的任务
     * @param callback 处理结果的回调
     * @param <T> 结果类型
     */
    public static <T> void runOnBackground(final Task<T> task, final Callback<T> callback) {
        executorService.execute(() -> {
            try {
                final T result = task.run();
                mainHandler.post(() -> callback.onComplete(result));
            } catch (final Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    public interface Task<T> {
        T run() throws Exception;
    }

    public interface Callback<T> {
        void onComplete(T result);
        void onError(Exception e);
    }
}
