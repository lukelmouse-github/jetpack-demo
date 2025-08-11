package com.example.feature.home.ui.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.common.base.BaseViewModel
import com.example.core.common.utils.ToastUtils
import com.example.core.net.model.NetResult
import com.example.feature.home.model.project.ProjectTabItem
import com.example.feature.home.repository.ProjectRepository
import kotlinx.coroutines.launch

/**
 * 项目ViewModel
 */
class ProjectViewModel(private val projectRepository: ProjectRepository) : BaseViewModel() {

    // Tab数据
    private val _tabDataList = MutableLiveData<List<ProjectTabItem>>()
    val tabDataList: LiveData<List<ProjectTabItem>> = _tabDataList

    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * 获取Tab数据
     */
    fun getTabData() {
        if (_isLoading.value == true) return

        _isLoading.value = true
        viewModelScope.launch {
            when (val result = projectRepository.getTabData()) {
                is NetResult.Success -> {
                    _tabDataList.value = result.data
                }
                is NetResult.Error -> {
                    ToastUtils.showToast("项目Tab数据加载失败: ${result.exception.msg}")
                }
            }
            _isLoading.value = false
        }
    }
}

