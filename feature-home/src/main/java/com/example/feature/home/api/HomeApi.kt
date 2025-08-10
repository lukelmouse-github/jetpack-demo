package com.example.feature.home.api

import com.example.core.net.model.BaseModel
import com.example.feature.home.model.Article
import com.example.feature.home.model.ArticleList
import com.example.feature.home.model.Banner
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 首页API接口
 */
interface HomeApi {

    /**
     * 获取首页Banner
     */
    @GET("banner/json")
    suspend fun getBanner(): BaseModel<List<Banner>>

    /**
     * 获取首页文章列表
     * @param page 页码，从0开始
     */
    @GET("article/list/{page}/json")
    suspend fun getArticleList(@Path("page") page: Int): BaseModel<ArticleList>

    /**
     * 获取置顶文章
     */
    @GET("article/top/json")
    suspend fun getTopArticles(): BaseModel<List<Article>>
}

