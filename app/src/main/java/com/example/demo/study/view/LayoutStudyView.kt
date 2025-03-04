package com.example.demo.study.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.drake.logcat.LogCat

class LayoutStudyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        LogCat.e("onDraw")

        // 添加调用栈跟踪
//        LogCat.e("onDraw Stack trace: ${Exception().stackTraceToString()}")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        LogCat.e("onLayout left = $left top = $top right = $right bottom = $bottom")
    }

    private fun getMode(measureSpec: Int): String {
        return when (View.MeasureSpec.getMode(measureSpec)) {
            View.MeasureSpec.UNSPECIFIED -> "UNSPECIFIED"
            View.MeasureSpec.EXACTLY -> "EXACTLY"
            View.MeasureSpec.AT_MOST -> "AT_MOST"
            else -> "UNKNOWN"
        }
    }

    private fun getSize(measureSpec: Int): Int {
        return View.MeasureSpec.getSize(measureSpec)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        LogCat.e("onMeasure >>>>>>>>")
        LogCat.e("onMeasure width = ${measuredWidth} widthMode = ${getMode(widthMeasureSpec)} widthSize = ${getSize(widthMeasureSpec)} " +
                "\nheight = ${measuredHeight} heightMode = ${getMode(heightMeasureSpec)} heightSize = ${getSize(heightMeasureSpec)}")
        // 添加调用栈跟踪
        LogCat.e("onMeasure Stack trace: ${Exception().stackTraceToString()}")
        LogCat.e("onMeasure <<<<<<<<")
    }
}