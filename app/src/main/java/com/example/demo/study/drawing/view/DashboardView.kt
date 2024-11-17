package com.example.demo.study.drawing.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View
import com.drake.logcat.LogCat
import com.example.common.utils.px

val RADIUS = 100f.px
class TestView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG) // 抗锯齿，边缘是半透明的，修改了原先的像素，所以默认不开。
    private val path = Path()
    lateinit var pathMeasure: PathMeasure


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh) // 尺寸改变时调用
        path.reset()
        path.addCircle(width / 2f, height / 2f, RADIUS, Path.Direction.CW)
        // 方向，填充还是镂空？
        // clockwise 顺时针
        // counterclockwise 逆时针
        path.addRect(width / 2f - RADIUS, height / 2f, width / 2f + RADIUS,
            height / 2 + 2 * RADIUS, Path.Direction.CW)
        path.addCircle(width / 2f, height / 2f, RADIUS * 1.5f, Path.Direction.CW)

//        path.fillType = Path.FillType.WINDING // 默认的填充方式，一个方向画圈的，内部都填充，从当前一个点出发射线出去
        path.fillType = Path.FillType.EVEN_ODD // 镂空优先用这个，不管方向的，如果射出去一个点，就内部，两个点就外部。
//        path.fillType = Path.FillType.INVERSE_EVEN_ODD // 反规则。
        pathMeasure = PathMeasure(path, false) // 路径测量长度。
        LogCat.d("pathMeasure: " + pathMeasure.length)



    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(path, paint)
    }
}