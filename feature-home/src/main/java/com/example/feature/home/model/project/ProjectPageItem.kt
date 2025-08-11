package com.example.feature.home.model.project

import com.squareup.moshi.JsonClass

/**
 * 项目分页数据模型
 */
@JsonClass(generateAdapter = true)
data class ProjectPageItem(
    val curPage: Int = 0,
    val size: Int = 0,
    val total: Int = 0,
    val pageCount: Int = 0,
    val over: Boolean = false,
    val datas: List<ProjectItemSub> = emptyList()
)

