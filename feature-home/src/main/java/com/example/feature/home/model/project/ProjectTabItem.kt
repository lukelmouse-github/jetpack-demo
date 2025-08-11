package com.example.feature.home.model.project

import com.squareup.moshi.JsonClass

/**
 * 项目Tab项数据模型
 */
@JsonClass(generateAdapter = true)
data class ProjectTabItem(
    val id: Int = 0,
    val name: String = ""
)

