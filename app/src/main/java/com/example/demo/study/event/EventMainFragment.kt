package com.example.demo.study.event

import android.view.MotionEvent
import com.example.common.base.BaseFragment
import com.example.common.log.ALog
import com.example.common.routers.Router
import com.example.common.routers.RouterPath
import com.example.demo.R
import com.example.demo.databinding.FragmentEventMainBinding
import com.example.demo.databinding.FragmentStudyMainBinding
import com.therouter.router.Route

@Route(path = RouterPath.STUDY_EVENT, description = "事件分发流程")
class EventMainFragment : BaseFragment<FragmentEventMainBinding>(R.layout.fragment_event_main) {
    private lateinit var adapter: EventAdapter
    override fun initView() {
        super.initView()

        // 初始化RecyclerView和适配器
        adapter = EventAdapter()
        binding.recyclerView.adapter = adapter

        // 为Fragment根视图添加触摸事件监听，记录事件分发
        binding.main.setOnTouchListener { v, event ->
            val action = when (event.action) {
                MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
                MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
                MotionEvent.ACTION_UP -> "ACTION_UP"
                MotionEvent.ACTION_CANCEL -> "ACTION_CANCEL"
                else -> "OTHER"
            }
            ALog.d("EventMainFragment root view onTouch: $action")
            false // 返回false表示不消费事件，继续传递
        }
    }

    override fun initData() {
        super.initData()
    }

}