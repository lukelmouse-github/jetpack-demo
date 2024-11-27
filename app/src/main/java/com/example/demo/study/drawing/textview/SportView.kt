package com.example.demo.study.drawing.textview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetrics
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.load.engine.Resource
import com.example.common.utils.dp
import com.example.demo.R


private val CIRCLE_COLOR = Color.parseColor("#90A4AE")
private val HIGHLIGHT_COLOR = Color.parseColor("#FF4081")
private val RING_WIDTH = 20.dp
private val RADIUS = 150.dp
class SportView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 100.dp
        typeface = ResourcesCompat.getFont(context, R.font.font)
        isFakeBoldText = true // 不是粗版的粗体，是描粗了，假的粗。。适合fontWeight？
        textAlign = Paint.Align.CENTER
    }
//    private val bounds = Rect()
    private val fontMetrics = FontMetrics()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制环
        paint.style = Paint.Style.STROKE
        paint.color = CIRCLE_COLOR
        paint.strokeWidth = RING_WIDTH
        canvas.drawCircle(width / 2f, height / 2f, RADIUS, paint)

        // 绘制进度条
        paint.color = HIGHLIGHT_COLOR
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(width / 2f - RADIUS, height / 2f - RADIUS,
            width / 2f + RADIUS, height / 2f + RADIUS,
            -90f, 225f, false, paint)

        // 绘制文字
        paint.style = Paint.Style.FILL
//        paint.getTextBounds("abab", 0, 4, rect) 可以直接算出来文字绘制的矩形坐标。
//        paint.getTextBounds("abab", 0, "abab".length, bounds)
        paint.getFontMetrics(fontMetrics)
        // 减去这个高度的偏移量就纵向剧中了。  (bounds.top + bounds.bottom) / 2f ,这俩高度有点飘，用fontMetrics.ascent + fontMetrics.descent是最好的。
        canvas.drawText("aaaa", width / 2f, height / 2f - (fontMetrics.ascent + fontMetrics.descent) / 2f, paint) // 记住baseline是在文字的底部
        /**
         * 文字的五条线。
         * top
         * ascent
         *
         * baseline
         * descent
         * bottom
         */
    }
}