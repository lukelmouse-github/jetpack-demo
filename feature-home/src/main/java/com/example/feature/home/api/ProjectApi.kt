package com.example.feature.home.api

import com.example.core.net.model.BaseModel
import com.example.feature.home.model.project.ProjectPageItem
import com.example.feature.home.model.project.ProjectTabItem
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 项目API接口
 */
interface ProjectApi {

    /**
     * 获取项目Tab数据
     */
    @GET("project/tree/json")
    suspend fun getTabData(): BaseModel<List<ProjectTabItem>>

    /**
     * 获取项目Tab项目列表数据
     * @param page 页码，从1开始
     * @param cid 分类ID
     */
    @GET("project/list/{page}/json")
    suspend fun getTabItemPageData(@Path("page") page: Int, @retrofit2.http.Query("cid") cid: Int): BaseModel<ProjectPageItem>
}

