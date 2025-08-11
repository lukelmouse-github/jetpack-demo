package com.example.feature.home.ui.project

import android.graphics.Typeface
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.core.common.base.BaseFragment
import com.example.core.common.utils.ALog
import com.example.feature.home.R
import com.example.feature.home.databinding.FragmentProjectBinding
import com.example.feature.home.model.project.ProjectTabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.therouter.router.Route
import org.koin.android.ext.android.get

/**
 * 项目Fragment
 */
@Route(path = "/project/fragment")
class ProjectFragment : BaseFragment<ProjectViewModel, FragmentProjectBinding>() {

    override val mViewModel: ProjectViewModel by lazy { get<ProjectViewModel>() }


    private var mData: List<ProjectTabItem>? = null
    private lateinit var mediator: TabLayoutMediator
    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager: ViewPager2

    override fun initData() {
        ALog.d("ProjectFragment", "initData")
        // 加载Tab数据
        mViewModel.getTabData()
    }

    override fun initView() {
        ALog.d("ProjectFragment", "initView")

        // 获取视图引用
        mViewPager = mViewBinding.viewPager
        mTabLayout = mViewBinding.tabLayout

        // 观察数据变化
        observeData()

        // 设置Tab选择监听
        setupTabLayout()
    }

    /**
     * 观察数据变化
     */
    private fun observeData() {
        // 观察Tab数据
        mViewModel.tabDataList.observe(this) { tabList ->
            ALog.d("ProjectFragment", "Tab数据更新: ${tabList.size}个")
            mData = tabList
            setupViewPager()
        }

        // 观察加载状态
        mViewModel.isLoading.observe(this) { isLoading ->
            // 可以在这里显示/隐藏Loading
            ALog.d("ProjectFragment", "Loading状态: $isLoading")
        }
    }

    /**
     * 设置TabLayout
     */
    private fun setupTabLayout() {
        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Tab重复选择
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Tab取消选择时，恢复普通样式
                tab?.customView?.let { customView ->
                    if (customView is android.widget.TextView) {
                        customView.textSize = 14f
                        customView.typeface = Typeface.DEFAULT
                    }
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Tab选择时，设置加粗样式
                tab?.customView?.let { customView ->
                    if (customView is android.widget.TextView) {
                        customView.textSize = 16f
                        customView.typeface = Typeface.DEFAULT_BOLD
                    }
                }
            }
        })
    }

    /**
     * 设置ViewPager
     */
    private fun setupViewPager() {
        val data = mData ?: return

        // 设置ViewPager适配器
        mViewPager.adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun getItemCount(): Int {
                return data.size
            }

            override fun createFragment(position: Int): Fragment {
                val item = data[position]
                return TabItemFragment.newInstance(item.id)
            }
        }

        // 设置ViewPager离屏页面限制
        // 增加缓存页面数量，提升切换体验
        mViewPager.offscreenPageLimit = 2

        // 关联TabLayout和ViewPager2
        mediator = TabLayoutMediator(
            mTabLayout,
            mViewPager,
            true
        ) { tab, position ->
            // 创建自定义Tab视图
            tab.customView = createTabView(position)
        }

        mediator.attach()
    }

    /**
     * 创建Tab视图
     */
    private fun createTabView(position: Int): android.view.View {
        val data = mData
        if (data != null && position < data.size) {
            val item = data[position]
            val textView = android.widget.TextView(requireContext())
            textView.text = item.name
            textView.textSize = 14f
            textView.typeface = Typeface.DEFAULT
            return textView
        }

        return android.widget.TextView(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::mediator.isInitialized) {
            mediator.detach()
        }
    }

    override fun getLayoutResId(): Int = R.layout.fragment_project

    companion object {
        fun newInstance(): ProjectFragment {
            return ProjectFragment()
        }
    }
}

