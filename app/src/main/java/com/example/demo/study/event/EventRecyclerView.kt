package com.example.demo.study.event

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.example.common.log.ALog

class EventRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = when (ev.action) {
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_CANCEL -> "ACTION_CANCEL"
            else -> "OTHER"
        }
        // 获取触摸点下的子视图位置
        val child = findChildViewUnder(ev.x, ev.y)
        val position = child?.let { getChildAdapterPosition(it) } ?: -1
        val itemInfo = if (position >= 0) "item位置: $position" else "未触碰到item"

        ALog.d("EventRecyclerView dispatchTouchEvent: $action, $itemInfo")
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val action = when (e.action) {
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_CANCEL -> "ACTION_CANCEL"
            else -> "OTHER"
        }
        // 获取触摸点下的子视图位置
        val child = findChildViewUnder(e.x, e.y)
        val position = child?.let { getChildAdapterPosition(it) } ?: -1
        val itemInfo = if (position >= 0) "item位置: $position" else "未触碰到item"

        ALog.d("EventRecyclerView onInterceptTouchEvent: $action, $itemInfo")
        return super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val action = when (e.action) {
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_CANCEL -> "ACTION_CANCEL"
            else -> "OTHER"
        }
        // 获取触摸点下的子视图位置
        val child = findChildViewUnder(e.x, e.y)
        val position = child?.let { getChildAdapterPosition(it) } ?: -1
        val itemInfo = if (position >= 0) "item位置: $position" else "未触碰到item"

        ALog.d("EventRecyclerView onTouchEvent: $action, $itemInfo")
        return super.onTouchEvent(e)
    }
}