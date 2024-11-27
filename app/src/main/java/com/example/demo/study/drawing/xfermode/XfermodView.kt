package com.example.demo.study.drawing.xfermode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.common.utils.dp

/**
 * Xfermode 的含义及底层实现
 * Xfermode 是 Android 中用于定义绘制操作中源像素（即要绘制的内容）和目标像素（即已经存在的内容）如何混合的机制。
 * 通过设置 Paint 的 xfermode 属性，可以实现各种图形混合效果，如遮罩、叠加、剪切等。
 *
 * 底层实现
 * Xfermode 的底层依赖于图形渲染引擎（如 Skia）提供的混合算法。
 * 以 PorterDuffXfermode 为例，它基于 Porter-Duff 混合模式，通过不同的模式参数（如 SRC_IN、DST_OVER 等）来决定源像素和目标像素的组合方式。
 * 这些混合模式在硬件加速下高效执行，确保渲染性能。
 * 为什么不直接使用 Canvas 一层层绘制
 * 虽然理论上可以通过多次使用 Canvas 的绘制操作来实现类似的视觉效果，但这通常会带来以下问题：
 * 复杂性增加：手动管理多个绘制步骤和图层顺序，代码更加复杂且易出错。
 * 性能开销：多次绘制可能导致额外的计算和内存开销，尤其是在需要频繁更新绘制内容时。
 * 效果限制：某些复杂的混合效果难以通过简单的绘制操作实现，而 Xfermode 提供了更灵活和强大的混合能力。
 * 示例对比
 * 使用 Xfermode 实现遮罩效果：
 *
 * kt
 * paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
 * canvas.drawBitmap(sourceBitmap, 0f, 0f, paint)
 * 通过 Canvas 多次绘制实现相同效果，则需要手动处理图层和像素计算，代码复杂且效率较低。
 * 综上所述，使用 Xfermode 不仅简化了代码逻辑，还能充分利用底层优化，提供高效且多样的图形混合效果。
 */
private val XFERMODE = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
class XfermodView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * 官网的教程是两个bitmap画的，如果直接drawRect和drawOval会出错，跟官网的图像不一样。
     */

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bounds = RectF(150f.dp, 50f.dp, 300f.dp, 250f.dp)

    private val circleBitmap = Bitmap.createBitmap(150f.dp.toInt(), 150f.dp.toInt(), Bitmap.Config.ARGB_8888)
    private val squareBitmap = Bitmap.createBitmap(150f.dp.toInt(), 150f.dp.toInt(), Bitmap.Config.ARGB_8888)

    init {
        val canvas = Canvas(circleBitmap)
        paint.color = Color.parseColor("#D81B60")
        canvas.drawOval(50f.dp, 0f.dp, 150f.dp, 100f.dp, paint)
        paint.color = Color.parseColor("#2196E3")
        canvas.setBitmap(squareBitmap)
        canvas.drawRect(0f.dp, 50f.dp, 100f.dp, 150f.dp, paint)
    }
    override fun onDraw(canvas: Canvas) {
        val count = canvas.saveLayer(bounds, null)
        canvas.drawBitmap(circleBitmap, 150f.dp, 50f.dp, paint)
        paint.xfermode = XFERMODE
        canvas.drawBitmap(squareBitmap, 150f.dp, 50f.dp, paint)
        paint.xfermode = null
        canvas.restoreToCount(count)
    }
}

