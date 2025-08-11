package com.example.feature.home.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.feature.home.databinding.ItemArticleBinding
import com.example.feature.home.databinding.ItemBannerBinding
import com.example.feature.home.model.Article
import com.example.feature.home.model.Banner

/**
 * 首页列表适配器
 */
class HomeAdapter(
    private val onArticleClick: (Article) -> Unit,
    private val onBannerClick: (Banner) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_BANNER = 0
        private const val TYPE_ARTICLE = 1
    }

    private val bannerList = mutableListOf<Banner>()
    private val articleList = mutableListOf<Article>()

    /**
     * 设置Banner数据
     */
    fun setBannerList(banners: List<Banner>) {
        bannerList.clear()
        bannerList.addAll(banners)
        notifyDataSetChanged()
    }

    /**
     * 设置文章数据
     */
    fun setArticleList(articles: List<Article>) {
        val oldSize = articleList.size
        articleList.clear()
        articleList.addAll(articles)

        if (oldSize == 0) {
            // 首次加载，刷新全部
            notifyDataSetChanged()
        } else {
            // 增量更新
            val bannerCount = if (bannerList.isNotEmpty()) 1 else 0
            notifyItemRangeChanged(bannerCount, articleList.size)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && bannerList.isNotEmpty()) {
            TYPE_BANNER
        } else {
            TYPE_ARTICLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_BANNER -> {
                val binding = ItemBannerBinding.inflate(inflater, parent, false)
                BannerViewHolder(binding, onBannerClick)
            }
            else -> {
                val binding = ItemArticleBinding.inflate(inflater, parent, false)
                ArticleViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BannerViewHolder -> {
                holder.bind(bannerList)
            }
            is ArticleViewHolder -> {
                val articlePosition = if (bannerList.isNotEmpty()) position - 1 else position
                if (articlePosition < articleList.size) {
                    holder.bind(articleList[articlePosition], onArticleClick)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        val bannerCount = if (bannerList.isNotEmpty()) 1 else 0
        return bannerCount + articleList.size
    }

    /**
     * Banner ViewHolder
     */
    class BannerViewHolder(
        private val binding: ItemBannerBinding,
        private val onBannerClick: (Banner) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(banners: List<Banner>) {
            if (banners.isNotEmpty()) {
                // 创建Banner适配器
                val bannerAdapter = HomeBannerAdapter(banners) { banner ->
                    onBannerClick(banner)
                }

                // 设置Banner适配器
                binding.bannerView.setAdapter(bannerAdapter)

                // 开始轮播
                binding.bannerView.start()
            }
        }
    }

    /**
     * 文章 ViewHolder
     */
    class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article, onArticleClick: (Article) -> Unit) {
            binding.article = article
            binding.executePendingBindings()

            binding.root.setOnClickListener {
                onArticleClick(article)
            }
        }
    }
}

