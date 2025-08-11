package com.example.feature.home.ui.home

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.core.common.base.BaseFragment
import com.example.core.common.utils.ALog
import com.example.feature.home.R
import com.example.feature.home.databinding.FragmentHomeBinding
import com.example.feature.home.ui.home.adapter.HomeAdapter
import com.therouter.router.Route
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 首页Fragment
 */
@Route(path = "/home/fragment")
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override val mViewModel: HomeViewModel by viewModel()
    private lateinit var homeAdapter: HomeAdapter

    override fun initData() {
        ALog.d("HomeFragment", "initData")
        // 初始化适配器
        setupRecyclerView()
        // 加载数据
        mViewModel.initData()
    }

    override fun initView() {
        ALog.d("HomeFragment", "initView")
        // 设置DataBinding
        mViewBinding.viewModel = mViewModel

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
        // 观察Banner数据
        mViewModel.bannerList.observe(this) { banners ->
            ALog.d("HomeFragment", "Banner数据更新: ${banners.size}个")
            homeAdapter.setBannerList(banners)
        }

        // 观察文章列表数据
        mViewModel.articleList.observe(this) { articles ->
            ALog.d("HomeFragment", "文章数据更新: ${articles.size}个")
            homeAdapter.setArticleList(articles)
        }

        // 观察刷新状态
        mViewModel.isRefreshing.observe(this) { isRefreshing ->
            if (!isRefreshing) {
                mViewBinding.swipeRefresh.isRefreshing = false
            }
        }
    }

    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter { article ->
            // 点击文章回调
            ALog.d("HomeFragment", "点击文章: ${article.title}")
            // 跳转到WebView页面
            com.example.core.webview.WebViewActivity.start(
                requireContext(),
                article.title,
                article.link
            )
        }

        mViewBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }
    }

    /**
     * 设置下拉刷新
     */
    private fun setupSwipeRefresh() {
        mViewBinding.swipeRefresh.setOnRefreshListener {
            mViewModel.refreshArticleList()
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

    override fun getLayoutResId(): Int = R.layout.fragment_home

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}

