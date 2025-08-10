package com.example.core.common.utils

import android.util.Log

/**
 * 全局日志工具类
 * 基于 AppContext 提供统一的日志管理
 */
object ALog {

    private const val DEFAULT_TAG = "AppLog"

    /**
     * Debug 级别日志
     */
    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    /**
     * Debug 级别日志（使用默认 TAG）
     */
    fun d(message: String) {
        d(DEFAULT_TAG, message)
    }

    /**
     * Info 级别日志
     */
    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    /**
     * Info 级别日志（使用默认 TAG）
     */
    fun i(message: String) {
        i(DEFAULT_TAG, message)
    }

    /**
     * Warning 级别日志
     */
    fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    /**
     * Warning 级别日志（使用默认 TAG）
     */
    fun w(message: String) {
        w(DEFAULT_TAG, message)
    }

    /**
     * Error 级别日志
     */
    fun e(tag: String, message: String) {
        Log.e(tag, message)
    }

    /**
     * Error 级别日志（使用默认 TAG）
     */
    fun e(message: String) {
        e(DEFAULT_TAG, message)
    }

    /**
     * Error 级别日志（带异常）
     */
    fun e(tag: String, message: String, throwable: Throwable) {
        Log.e(tag, message, throwable)
    }

    /**
     * Error 级别日志（带异常，使用默认 TAG）
     */
    fun e(message: String, throwable: Throwable) {
        e(DEFAULT_TAG, message, throwable)
    }

    /**
     * Verbose 级别日志
     */
    fun v(tag: String, message: String) {
        Log.v(tag, message)
    }

    /**
     * Verbose 级别日志（使用默认 TAG）
     */
    fun v(message: String) {
        v(DEFAULT_TAG, message)
    }

    /**
     * 打印当前线程信息
     */
    fun printCurrentThread(tag: String = DEFAULT_TAG) {
        val thread = Thread.currentThread()
        Log.d(tag, "Current Thread: ${thread.name}, ID: ${thread.id}")
    }
}