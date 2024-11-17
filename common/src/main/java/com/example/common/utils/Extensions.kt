package com.example.common.utils

import android.util.TypedValue

val Float.px: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        UIUtils.getContext().resources.displayMetrics
    )