package com.example.demo.study.drawing.textview

import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.common.utils.dp

class MultilineTextView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    // lorem ipsum

    val text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
    val paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
        textSize = 16.dp
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val staticLayout = StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false)
        staticLayout.draw(canvas)
    }
}