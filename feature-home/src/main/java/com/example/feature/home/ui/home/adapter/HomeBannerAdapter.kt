package com.example.feature.home.ui.home.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.feature.home.model.Banner
import com.youth.banner.adapter.BannerAdapter

/**
 * Banner轮播图适配器
 */
class HomeBannerAdapter(
    mDatas: List<Banner>,
    private val onBannerClick: (Banner) -> Unit
) : BannerAdapter<Banner, HomeBannerAdapter.BannerViewHolder>(mDatas) {

    /**
     * 创建ViewHolder
     */
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val imageView = ImageView(parent.context)
        // 设置为match_parent，这是ViewPager2的强制要求
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return BannerViewHolder(imageView)
    }

    /**
     * 绑定数据
     */
    override fun onBindView(holder: BannerViewHolder, data: Banner, position: Int, size: Int) {
        // 使用Glide加载图片
        Glide.with(holder.imageView.context)
            .load(data.imagePath)
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(24)) // 圆角
                    .placeholder(android.R.drawable.ic_menu_gallery) // 占位图
                    .error(android.R.drawable.ic_menu_close_clear_cancel) // 错误图
            )
            .into(holder.imageView)

        // 设置点击事件
        holder.imageView.setOnClickListener {
            onBannerClick(data)
        }
    }

    /**
     * Banner ViewHolder
     */
    class BannerViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}

