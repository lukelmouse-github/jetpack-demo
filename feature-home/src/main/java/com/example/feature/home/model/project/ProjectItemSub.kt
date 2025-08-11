package com.example.feature.home.model.project

import com.squareup.moshi.JsonClass

/**
 * 项目子项数据模型
 */
@JsonClass(generateAdapter = true)
data class ProjectItemSub(
    val id: Int = 0,
    val title: String = "",
    val desc: String = "",
    val author: String = "",
    val shareUser: String = "",
    val link: String = "",
    val projectLink: String = "",
    val envelopePic: String = "",
    val niceDate: String = "",
    val publishTime: Long = 0,
    val collect: Boolean = false,
    val fresh: Boolean = false,
    val zan: Int = 0,
    val visible: Int = 0,
    val tags: List<Tag> = emptyList()
) {
    @JsonClass(generateAdapter = true)
    data class Tag(
        val name: String = "",
        val url: String = ""
    )
}

