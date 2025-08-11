package com.example.feature.home.ui.project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feature.home.databinding.ItemProjectBinding
import com.example.feature.home.model.project.ProjectItemSub

/**
 * 项目列表适配器
 */
class ProjectAdapter(
    private val onProjectClick: (ProjectItemSub) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private val projectList = mutableListOf<ProjectItemSub>()

    /**
     * 设置项目数据
     */
    fun setProjectList(projects: List<ProjectItemSub>) {
        val oldSize = projectList.size
        projectList.clear()
        projectList.addAll(projects)

        if (oldSize == 0) {
            // 首次加载，刷新全部
            notifyDataSetChanged()
        } else {
            // 增量更新
            notifyItemRangeChanged(0, projectList.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProjectBinding.inflate(inflater, parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        if (position < projectList.size) {
            holder.bind(projectList[position], onProjectClick)
        }
    }

    override fun getItemCount(): Int = projectList.size

    /**
     * 项目 ViewHolder
     */
    class ProjectViewHolder(private val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: ProjectItemSub, onProjectClick: (ProjectItemSub) -> Unit) {
            // 设置数据绑定
            binding.project = project
            binding.executePendingBindings()

            // 加载项目图片
            if (project.envelopePic.isNotEmpty()) {
                Glide.with(binding.ivProject.context)
                    .load(project.envelopePic)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivProject)
            } else {
                binding.ivProject.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // 设置点击事件
            binding.root.setOnClickListener {
                onProjectClick(project)
            }
        }
    }
}

