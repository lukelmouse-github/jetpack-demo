package com.example.feature.home.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.common.base.BaseViewModel
import com.example.core.common.utils.ToastUtils
import com.example.core.net.model.NetResult
import com.example.feature.home.model.Article
import com.example.feature.home.model.Banner
import com.example.feature.home.repository.HomeRepository
import kotlinx.coroutines.launch

/**
 * 首页ViewModel
 */
class HomeViewModel(private val homeRepository: HomeRepository) : BaseViewModel() {

    // Banner数据
    private val _bannerList = MutableLiveData<List<Banner>>()
    val bannerList: LiveData<List<Banner>> = _bannerList

    // 文章列表数据
    private val _articleList = MutableLiveData<List<Article>>()
    val articleList: LiveData<List<Article>> = _articleList

    // 刷新状态
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    // 加载更多状态
    private val _isLoadingMore = MutableLiveData<Boolean>()
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private var currentPage = 0
    private val allArticles = mutableListOf<Article>()

    /**
     * 初始化数据
     */
    fun initData() {
        loadBanner()
        refreshArticleList()
    }

    /**
     * 加载Banner
     */
    private fun loadBanner() {
        viewModelScope.launch {
            when (val result = homeRepository.getBanner()) {
                is NetResult.Success -> {
                    _bannerList.value = result.data
                }
                is NetResult.Error -> {
                    ToastUtils.showToast("Banner加载失败: ${result.exception.msg}")
                }
            }
        }
    }

    /**
     * 刷新文章列表
     */
    fun refreshArticleList() {
        _isRefreshing.value = true
        currentPage = 0
        allArticles.clear()

        viewModelScope.launch {
            // 先加载置顶文章
            loadTopArticles()
            // 再加载普通文章
            loadArticleList(currentPage)
            _isRefreshing.value = false
        }
    }

    /**
     * 加载更多文章
     */
    fun loadMore() {
        if (_isLoadingMore.value == true) return

        _isLoadingMore.value = true
        currentPage++

        viewModelScope.launch {
            loadArticleList(currentPage)
            _isLoadingMore.value = false
        }
    }

    /**
     * 加载置顶文章
     */
    private suspend fun loadTopArticles() {
        when (val result = homeRepository.getTopArticles()) {
            is NetResult.Success -> {
                // 标记置顶文章
                val topArticles = result.data.map { it.copy(top = true) }
                allArticles.addAll(topArticles)
            }
            is NetResult.Error -> {
                // 置顶文章加载失败不影响主流程
            }
        }
    }

    /**
     * 加载文章列表
     */
    private suspend fun loadArticleList(page: Int) {
        when (val result = homeRepository.getArticleList(page)) {
            is NetResult.Success -> {
                if (page == 0) {
                    // 首页数据，清空后添加
                    allArticles.removeAll { !it.top }
                }
                allArticles.addAll(result.data.datas)
                _articleList.value = allArticles.toList()
            }
            is NetResult.Error -> {
                if (page > 0) {
                    // 加载更多失败，页码回退
                    currentPage--
                }
                ToastUtils.showToast("文章加载失败: ${result.exception.msg}")
            }
        }
    }
}

