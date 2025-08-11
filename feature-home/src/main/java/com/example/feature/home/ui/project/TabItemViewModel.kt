package com.example.feature.home.ui.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.common.base.BaseViewModel
import com.example.core.common.utils.ToastUtils
import com.example.core.net.model.NetResult
import com.example.feature.home.model.project.ProjectItemSub
import com.example.feature.home.repository.ProjectRepository
import kotlinx.coroutines.launch

/**
 * Tab项目ViewModel
 */
class TabItemViewModel(private val projectRepository: ProjectRepository) : BaseViewModel() {

    // 项目列表数据
    private val _projectList = MutableLiveData<List<ProjectItemSub>>()
    val projectList: LiveData<List<ProjectItemSub>> = _projectList

    // 刷新状态
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    // 加载更多状态
    private val _isLoadingMore = MutableLiveData<Boolean>()
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private var currentPage = 1
    private var currentCid = 0
    private val allProjects = mutableListOf<ProjectItemSub>()

    /**
     * 设置分类ID
     */
    fun setCid(cid: Int) {
        currentCid = cid
    }

    /**
     * 初始化数据
     */
    fun initData() {
        refreshProjectList()
    }

    /**
     * 刷新项目列表
     */
    fun refreshProjectList() {
        _isRefreshing.value = true
        currentPage = 1
        allProjects.clear()

        viewModelScope.launch {
            loadProjectList(currentPage)
            _isRefreshing.value = false
        }
    }

    /**
     * 加载更多项目
     */
    fun loadMore() {
        if (_isLoadingMore.value == true) return

        _isLoadingMore.value = true
        currentPage++

        viewModelScope.launch {
            loadProjectList(currentPage)
            _isLoadingMore.value = false
        }
    }

    /**
     * 加载项目列表
     */
    private suspend fun loadProjectList(page: Int) {
        when (val result = projectRepository.getTabItemPageData(page, currentCid)) {
            is NetResult.Success -> {
                if (page == 1) {
                    // 首页数据，清空后添加
                    allProjects.clear()
                }
                allProjects.addAll(result.data.datas)
                _projectList.value = allProjects.toList()
            }
            is NetResult.Error -> {
                if (page > 1) {
                    // 加载更多失败，页码回退
                    currentPage--
                }
                ToastUtils.showToast("项目加载失败: ${result.exception.msg}")
            }
        }
    }
}

