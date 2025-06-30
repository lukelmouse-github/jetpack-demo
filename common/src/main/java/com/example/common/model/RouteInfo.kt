package com.example.common.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RouteInfo(
    @Json(name = "path")
    val path: String,
    
    @Json(name = "className")
    val className: String,
    
    @Json(name = "action")
    val action: String,
    
    @Json(name = "description")
    val description: String,
    
    @Json(name = "params")
    val params: Map<String, Any> = emptyMap()
)