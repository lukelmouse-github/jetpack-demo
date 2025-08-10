package com.example.feature.home.model

import com.squareup.moshi.JsonClass

/**
 * 文章列表分页数据模型
 */
@JsonClass(generateAdapter = true)
data class ArticleList(
    val curPage: Int = 0,
    val pageCount: Int = 0,
    val size: Int = 0,
    val total: Int = 0,
    val offset: Int = 0,
    val over: Boolean = false,
    val datas: List<Article> = emptyList()
)

