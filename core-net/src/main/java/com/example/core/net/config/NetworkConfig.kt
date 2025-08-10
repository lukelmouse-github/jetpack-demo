package com.example.core.net.config

/**
 * 网络配置类
 * 统一管理网络相关配置
 */
object NetworkConfig {

    /**
     * 连接超时时间（秒）
     */
    const val CONNECT_TIMEOUT = 30L

    /**
     * 读取超时时间（秒）
     */
    const val READ_TIMEOUT = 30L

    /**
     * 写入超时时间（秒）
     */
    const val WRITE_TIMEOUT = 30L

    /**
     * 是否启用日志
     */
    var isLogEnabled = true

    /**
     * 基础 URL
     */
    var baseUrl = "https://www.wanandroid.com/"

    /**
     * 请求头配置
     */
    val defaultHeaders = mapOf(
        "Content-Type" to "application/json",
        "Accept" to "application/json"
    )
}

