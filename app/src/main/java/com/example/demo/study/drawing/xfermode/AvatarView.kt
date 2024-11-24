package com.example.demo.study.drawing.xfermode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.common.utils.px
import com.example.demo.R


private val IMAGE_WIDTH = 200f.px
private val IMAGE_PADDING = 20f.px
private val AVATAR_PADDING = 10f.px
private val XFETMODE = PorterDuffXfermode(PorterDuff.Mode.SRC_IN) // 看官网文档，这些参数到底是啥意思
class AvatarView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bounds = RectF(IMAGE_PADDING, IMAGE_PADDING,
        IMAGE_PADDING + IMAGE_WIDTH, IMAGE_PADDING + IMAGE_WIDTH)

    override fun onDraw(canvas: Canvas) {
        val count = canvas.saveLayer(bounds, null) // 离屏缓冲，耗费资源，bounds要尽量小
        canvas.drawOval(IMAGE_PADDING, IMAGE_PADDING,
            IMAGE_PADDING + IMAGE_WIDTH, IMAGE_PADDING + IMAGE_WIDTH, paint) // 画椭圆
        paint.xfermode = XFETMODE
        canvas.drawBitmap(getAvatar(IMAGE_WIDTH.toInt()), IMAGE_PADDING, IMAGE_PADDING, paint)
        paint.xfermode = null // 恢复一下，好习惯
        canvas.restoreToCount(count)

        paint.color = Color.BLACK
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
        canvas.drawOval(IMAGE_PADDING, IMAGE_PADDING,
            IMAGE_PADDING + IMAGE_WIDTH, IMAGE_PADDING + IMAGE_WIDTH, paint) // 画椭圆
    }

    /**
     * 尺寸优化
     * 图片原来尺寸可能会很大，
     * 但是使用的图片是个固定宽，用options去优化，避免不需要的内存占用。
     * 高度，系统会自适应调整，所以只需要设定宽度
     */
    fun getAvatar(width: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // 只是读尺寸，具体的像素不读，这样子会非常快。
        BitmapFactory.decodeResource(resources, R.drawable.avatar, options)
        options.inJustDecodeBounds = false // 关闭他，开始读真正的图片
        options.inDensity = options.outWidth // 设置密度为原始图片的宽度
        options.inTargetDensity = width // 目标密度为自定义的宽度
        return BitmapFactory.decodeResource(resources, R.drawable.avatar, options)
    }
}