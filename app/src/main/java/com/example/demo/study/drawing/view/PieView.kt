package com.example.demo.study.drawing.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.common.utils.px
import kotlin.math.cos
import kotlin.math.sin


private val RADIUS = 150f.px
private val ANGLES = floatArrayOf(60f, 90f, 150f, 60f)
private val COLORS = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
private val OFFSET_LENGTH = 20f.px
class PieView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG) // 抗锯齿，边缘是半透明的，修改了原先的像素，所以默认不开。

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    }

    override fun onDraw(canvas: Canvas) {
        var startAngle = 0f
        for ((index, angle) in ANGLES.withIndex()) {
            paint.color = COLORS[index]
            if (index == 1) {
                canvas.save()
                canvas.translate(OFFSET_LENGTH * cos(Math.toRadians(startAngle + angle / 2f.toDouble()).toFloat()),
                    OFFSET_LENGTH * sin(Math.toRadians(startAngle + angle / 2f.toDouble()).toFloat())
                )
            }
            canvas.drawArc(width / 2f - RADIUS, height / 2f - RADIUS,
                width / 2f + RADIUS, height / 2f + RADIUS,
                startAngle, angle, true, paint)
            startAngle += angle
            if (index == 1) {
                canvas.restore()
            }
        }
    }
}