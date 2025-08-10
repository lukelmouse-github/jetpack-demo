package com.example.feature.home.ui.main

import androidx.fragment.app.Fragment
import com.example.core.common.base.BaseActivity
import com.example.core.common.utils.ALog
import com.example.feature.home.R
import com.example.feature.home.databinding.ActivityMainBinding
import com.therouter.TheRouter
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 主Activity - 管理底部Tab导航
 */
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    // ✅ 使用 Koin 依赖注入，自动管理生命周期
    override val mViewModel: MainViewModel by viewModel()

    // 当前显示的Fragment
    private var currentFragment: Fragment? = null

    // Fragment缓存，避免重复创建
    private val fragmentMap = mutableMapOf<String, Fragment>()

    override fun initData() {
        ALog.d("MainActivity", "initData")
    }

    override fun initView() {
        ALog.d("MainActivity", "initView")

        setupBottomNavigation()

        // 默认显示首页
        showFragment("/home/fragment")
    }

    /**
     * 设置底部导航
     */
    private fun setupBottomNavigation() {
        mViewBinding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    showFragment("/home/fragment")
                    true
                }
                R.id.navigation_project -> {
                    showFragment("/project/fragment")
                    true
                }
                R.id.navigation_navi -> {
                    showFragment("/navigation/fragment")
                    true
                }
                R.id.navigation_tree -> {
                    showFragment("/tree/fragment")
                    true
                }
                R.id.navigation_mine -> {
                    showFragment("/mine/fragment")
                    true
                }
                else -> false
            }
        }
    }

    /**
     * 显示Fragment（使用TheRouter + Fragment缓存机制）
     */
    private fun showFragment(path: String) {
        try {
            val targetFragment = getOrCreateFragment(path)
            if (targetFragment != null) {
                val transaction = supportFragmentManager.beginTransaction()

                // 隐藏当前Fragment
                currentFragment?.let {
                    transaction.hide(it)
                }

                // 如果Fragment已经添加过，则显示；否则添加
                if (targetFragment.isAdded) {
                    transaction.show(targetFragment)
                } else {
                    transaction.add(R.id.nav_host_fragment, targetFragment, path)
                }

                transaction.commitAllowingStateLoss()
                currentFragment = targetFragment
            } else {
                ALog.e("MainActivity", "无法创建Fragment: $path")
            }
        } catch (e: Exception) {
            ALog.e("MainActivity", "切换Fragment失败: ${e.message}")
        }
    }

    /**
     * 获取或创建Fragment（缓存机制）
     */
    private fun getOrCreateFragment(path: String): Fragment? {
        // 先从缓存中查找
        var fragment = fragmentMap[path]
        if (fragment != null) {
            return fragment
        }

        // 从FragmentManager中查找
        fragment = supportFragmentManager.findFragmentByTag(path)
        if (fragment != null) {
            fragmentMap[path] = fragment
            return fragment
        }

        // 使用TheRouter创建新Fragment
        fragment = createFragmentByPath(path)
        if (fragment != null) {
            fragmentMap[path] = fragment
        }

        return fragment
    }

    /**
     * 根据路径创建Fragment
     */
    private fun createFragmentByPath(path: String): Fragment? {
        return try {
            // 优先使用TheRouter创建真实Fragment
            @Suppress("UNCHECKED_CAST")
            val routerFragment = TheRouter.build(path).createFragment() as? Fragment
            if (routerFragment != null) {
                ALog.d("MainActivity", "使用TheRouter创建Fragment成功: $path")
                routerFragment
            } else {
                ALog.w("MainActivity", "TheRouter无法创建Fragment，使用占位Fragment: $path")
                createPlaceholderFragment(getTabNameByPath(path))
            }
        } catch (e: Exception) {
            ALog.e("MainActivity", "创建Fragment失败: $path, ${e.message}")
            // 异常时也使用占位Fragment
            createPlaceholderFragment(getTabNameByPath(path))
        }
    }

    /**
     * 创建占位Fragment
     */
    private fun createPlaceholderFragment(tabName: String): Fragment {
        return PlaceholderFragment.newInstance(tabName)
    }

    /**
     * 根据路径获取Tab名称
     */
    private fun getTabNameByPath(path: String): String {
        return when (path) {
            "/home/fragment" -> "首页"
            "/project/fragment" -> "项目"
            "/navigation/fragment" -> "导航"
            "/tree/fragment" -> "体系"
            "/mine/fragment" -> "我的"
            else -> "未知"
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_main
}