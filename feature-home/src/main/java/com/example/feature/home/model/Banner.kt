package com.example.feature.home.model

import com.squareup.moshi.JsonClass

/**
 * 首页Banner数据模型
 */
@JsonClass(generateAdapter = true)
data class Banner(
    val desc: String = "",
    val id: Int = 0,
    val imagePath: String = "",
    val isVisible: Int = 0,
    val order: Int = 0,
    val title: String = "",
    val type: Int = 0,
    val url: String = ""
)

