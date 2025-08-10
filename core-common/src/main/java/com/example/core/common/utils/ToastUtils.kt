package com.example.core.common.utils

import android.widget.Toast
import com.example.core.common.base.AppContext

object ToastUtils {
    fun showToast(message: String) {
        Toast.makeText(AppContext.application, message, Toast.LENGTH_SHORT).show()
    }
}