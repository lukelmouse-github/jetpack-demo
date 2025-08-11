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

    // 内存缓存
    private val tabDataCache = mutableMapOf<String, Pair<List<ProjectTabItem>, Long>>()
    private val projectDataCache = mutableMapOf<String, Pair<ProjectPageItem, Long>>()
    private val cacheTimeout = 10 * 60 * 1000L // 10分钟缓存过期

    /**
     * 获取项目Tab数据
     */
    suspend fun getTabData(): NetResult<List<ProjectTabItem>> {
        val cacheKey = "tab_data"

        // 检查缓存
        tabDataCache[cacheKey]?.let { (data, time) ->
            if (System.currentTimeMillis() - time < cacheTimeout) {
                return NetResult.Success(data)
            }
        }

        // 缓存失效或无缓存，请求网络
        return callRequest {
            handleResponse(projectApi.getTabData())
        }.also { result ->
            // 如果请求成功，更新缓存
            if (result is NetResult.Success) {
                tabDataCache[cacheKey] = Pair(result.data, System.currentTimeMillis())
            }
        }
    }

    /**
     * 获取项目Tab项目列表数据
     * @param page 页码，从1开始
     * @param cid 分类ID
     */
    suspend fun getTabItemPageData(page: Int, cid: Int): NetResult<ProjectPageItem> {
        // 只缓存第一页数据
        if (page == 1) {
            val cacheKey = "project_data_${cid}_$page"

            // 检查缓存
            projectDataCache[cacheKey]?.let { (data, time) ->
                if (System.currentTimeMillis() - time < cacheTimeout) {
                    return NetResult.Success(data)
                }
            }

            // 缓存失效或无缓存，请求网络
            return callRequest {
                handleResponse(projectApi.getTabItemPageData(page, cid))
            }.also { result ->
                // 如果请求成功，更新缓存
                if (result is NetResult.Success) {
                    projectDataCache[cacheKey] = Pair(result.data, System.currentTimeMillis())
                }
            }
        } else {
            // 非首页数据不缓存，直接请求
            return callRequest {
                handleResponse(projectApi.getTabItemPageData(page, cid))
            }
        }
    }
}

