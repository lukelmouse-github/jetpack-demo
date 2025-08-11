package com.example.feature.home.ui.project

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.core.common.base.BaseFragment
import com.example.core.common.utils.ALog
import com.example.feature.home.R
import com.example.feature.home.databinding.FragmentTabItemBinding
import com.example.feature.home.ui.project.adapter.ProjectAdapter
import org.koin.android.ext.android.get

/**
 * Tab项目Fragment
 */
class TabItemFragment : BaseFragment<TabItemViewModel, FragmentTabItemBinding>() {

    override val mViewModel: TabItemViewModel by lazy { get<TabItemViewModel>() }
    private lateinit var projectAdapter: ProjectAdapter
    private var cid: Int = 0

    companion object {
        private const val ARG_CID = "cid"

        fun newInstance(cid: Int): TabItemFragment {
            val fragment = TabItemFragment()
            val args = Bundle()
            args.putInt(ARG_CID, cid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initData() {
        ALog.d("TabItemFragment", "initData")

        // 获取传递的分类ID
        arguments?.let { args ->
            cid = args.getInt(ARG_CID, 0)
            mViewModel.setCid(cid)
        }

        // 初始化适配器
        setupRecyclerView()

        // 加载数据
        mViewModel.initData()
    }

    override fun initView() {
        ALog.d("TabItemFragment", "initView")

        // 设置下拉刷新
        setupSwipeRefresh()

        // 设置RecyclerView滚动监听
        setupScrollListener()

        // 观察数据变化
        observeData()
    }

    /**
     * 观察数据变化
     */
    private fun observeData() {
        // 观察项目列表数据
        mViewModel.projectList.observe(this) { projects ->
            ALog.d("TabItemFragment", "项目数据更新: ${projects.size}个")
            projectAdapter.setProjectList(projects)
        }

        // 观察刷新状态
        mViewModel.isRefreshing.observe(this) { isRefreshing ->
            if (!isRefreshing) {
                mViewBinding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        projectAdapter = ProjectAdapter { project ->
            // 点击项目回调
            ALog.d("TabItemFragment", "点击项目: ${project.title}")
            // 跳转到WebView页面
            com.example.core.webview.WebViewActivity.start(
                requireContext(),
                project.title,
                project.link
            )
        }

        mViewBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = projectAdapter
        }
    }

    /**
     * 设置下拉刷新
     */
    private fun setupSwipeRefresh() {
        mViewBinding.swipeRefreshLayout.setOnRefreshListener {
            mViewModel.refreshProjectList()
        }
    }

    /**
     * 设置滚动监听（用于加载更多）
     */
    private fun setupScrollListener() {
        mViewBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // 滚动到倒数第3个item时开始加载更多
                if (lastVisibleItem >= totalItemCount - 3 && dy > 0) {
                    mViewModel.loadMore()
                }
            }
        })
    }

    override fun getLayoutResId(): Int = R.layout.fragment_tab_item
}

