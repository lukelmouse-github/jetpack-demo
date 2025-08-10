package com.example.core.common.base

import android.app.Application

/**
 * 全局 Application Context 管理类
 * 提供全局的 Context 访问能力，避免内存泄漏
 */
object AppContext {

    /**
     * Application 实例
     * 使用 Delegates.notNull() 确保初始化后才能访问
     */
    lateinit var application: Application

    /**
     * 初始化 AppContext
     * @param app Application 实例
     */
    fun init(app: Application) {
        application = app
    }
}