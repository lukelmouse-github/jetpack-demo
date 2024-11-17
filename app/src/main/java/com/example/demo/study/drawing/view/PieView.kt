package com.example.demo.study.drawing.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathDashPathEffect
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.common.utils.px
import kotlin.math.cos
import kotlin.math.sin


private const val OPEN_ANGLE = 120f
private const val MARK = 10
private val RADIUS = 150f.px
private val LENGTH = 120f.px
private val DASH_WIDTH = 2f.px
private val DASH_LENGTH = 5f.px
class PieView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG) // 抗锯齿，边缘是半透明的，修改了原先的像素，所以默认不开。
    private val dash = Path()
    private val path = Path()
    lateinit var pathEffect: PathDashPathEffect

    init {
        paint.strokeWidth = 3f.px
        paint.style = Paint.Style.STROKE
        dash.addRect(0f, 0f, DASH_WIDTH, DASH_LENGTH, Path.Direction.CW)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.e("luke", "onSizeChanged")
        path.reset()
        path.addArc(width / 2f - 150f.px, height / 2f - 150f.px, width / 2f + 150f.px, height / 2f + 150f.px,
            90 + OPEN_ANGLE / 2f, 360 - OPEN_ANGLE)
        val pathMeasure = PathMeasure(path, false)
        // 减去一个刻度的宽度，再除。20是20个刻度
        pathEffect = PathDashPathEffect(dash, (pathMeasure.length - DASH_WIDTH) / 20f, 0f, PathDashPathEffect.Style.ROTATE)
    }

    override fun onDraw(canvas: Canvas) {
        Log.e("luke", "onDraw")
        // 1. 画圆弧
        canvas.drawPath(path, paint)

        /** android自己把参数搞反了，我也写反把，这个advance和phase
         * 用这个特效来画，所以这个特效就没了。
         */
        paint.pathEffect = pathEffect
        /**
         * 所以画两边
         */
        // 2. 画刻度
        canvas.drawPath(path, paint)
        paint.pathEffect = null

        // 三角函数，画图算算吧，stopX 是余弦，stopY是正弦
        // 用角度，通过cos算x，y的偏移量。
        canvas.drawLine(width / 2f, height / 2f,
            width / 2f + LENGTH * cos(markToRadius(MARK)).toFloat(),
            height / 2f + LENGTH * sin(markToRadius(MARK)).toFloat(),
            paint)
    }

    private fun markToRadius(mark: Int) =
        Math.toRadians((90 + OPEN_ANGLE / 2f + (360 - OPEN_ANGLE) / 20f * mark).toDouble())
}