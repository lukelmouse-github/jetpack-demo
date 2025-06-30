package com.example.demo.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RouteItem(
    val path: String,
    val className: String,
    val action: String,
    val description: String,
    val params: Map<String, Any>
)