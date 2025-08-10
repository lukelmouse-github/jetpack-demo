package com.example.feature.home.repository

import com.example.core.net.client.RetrofitClient
import com.example.core.net.model.NetResult
import com.example.core.net.repository.BaseRepository
import com.example.feature.home.api.HomeApi
import com.example.feature.home.model.Article
import com.example.feature.home.model.ArticleList
import com.example.feature.home.model.Banner

/**
 * 首页数据仓库
 */
class HomeRepository : BaseRepository() {

    private val homeApi by lazy { RetrofitClient.create<HomeApi>() }

    /**
     * 获取首页Banner
     */
    suspend fun getBanner(): NetResult<List<Banner>> {
        return callRequest {
            handleResponse(homeApi.getBanner())
        }
    }

    /**
     * 获取首页文章列表
     * @param page 页码，从0开始
     */
    suspend fun getArticleList(page: Int): NetResult<ArticleList> {
        return callRequest {
            handleResponse(homeApi.getArticleList(page))
        }
    }

    /**
     * 获取置顶文章
     */
    suspend fun getTopArticles(): NetResult<List<Article>> {
        return callRequest {
            handleResponse(homeApi.getTopArticles())
        }
    }
}

