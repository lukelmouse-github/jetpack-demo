package com.example.demo.study.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.example.common.utils.dp
import com.example.common.utils.dpInt

class SquareImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(l, t, (l + 100).dpInt, (b + 100).dpInt)
    }

}