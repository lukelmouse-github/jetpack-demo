package com.example.feature.home.model

import com.squareup.moshi.JsonClass

/**
 * 文章数据模型
 */
@JsonClass(generateAdapter = true)
data class Article(
    val id: Int = 0,
    val title: String = "",
    val author: String = "",
    val shareUser: String = "",
    val desc: String = "",
    val link: String = "",
    val niceDate: String = "",
    val publishTime: Long = 0,
    val superChapterName: String = "",
    val chapterName: String = "",
    val collect: Boolean = false,
    val fresh: Boolean = false,
    val top: Boolean = false,
    val visible: Int = 0,
    val zan: Int = 0
)

