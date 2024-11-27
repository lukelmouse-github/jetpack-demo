package com.example.common.utils

import android.util.TypedValue

val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        UIUtils.getContext().resources.displayMetrics
    )

val Int.dp: Float
    get() = this.toFloat().dp