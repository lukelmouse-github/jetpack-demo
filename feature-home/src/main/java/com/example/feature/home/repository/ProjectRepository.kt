package com.example.feature.home.repository

import com.example.core.net.client.RetrofitClient
import com.example.core.net.model.NetResult
import com.example.core.net.repository.BaseRepository
import com.example.feature.home.api.ProjectApi
import com.example.feature.home.model.project.ProjectPageItem
import com.example.feature.home.model.project.ProjectTabItem

/**
 * 项目数据仓库
 */
class ProjectRepository : BaseRepository() {

    private val projectApi by lazy { RetrofitClient.create<ProjectApi>() }

    /**
     * 获取项目Tab数据
     */
    suspend fun getTabData(): NetResult<List<ProjectTabItem>> {
        return callRequest {
            handleResponse(projectApi.getTabData())
        }
    }

    /**
     * 获取项目Tab项目列表数据
     * @param page 页码，从1开始
     * @param cid 分类ID
     */
    suspend fun getTabItemPageData(page: Int, cid: Int): NetResult<ProjectPageItem> {
        return callRequest {
            handleResponse(projectApi.getTabItemPageData(page, cid))
        }
    }
}

