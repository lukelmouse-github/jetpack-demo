package com.example.common.utils

import android.content.Context
import com.example.common.model.RouteInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException

object RouteMapReader {
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val listType = Types.newParameterizedType(List::class.java, RouteInfo::class.java)
    private val adapter = moshi.adapter<List<RouteInfo>>(listType)
    
    /**
     * 从assets中读取routeMap.json文件并解析为RouteInfo列表
     */
    fun readRouteMap(context: Context): List<RouteInfo> {
        return try {
            val json = context.assets.open("therouter/routeMap.json").bufferedReader().use { it.readText() }
            adapter.fromJson(json) ?: emptyList()
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * 获取去重后的路由列表（相同path只保留一个）
     */
    fun getUniqueRoutes(context: Context): List<RouteInfo> {
        return readRouteMap(context)
            .distinctBy { it.path }
            .sortedBy { it.path }
            .filterNot { it.path.equals("/empty_shell_activity") }
    }
}