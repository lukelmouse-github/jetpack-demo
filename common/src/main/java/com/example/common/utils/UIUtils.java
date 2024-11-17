package com.example.common.utils;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;

import com.example.common.base.App;

public class UIUtils {

    public static Context getContext() {
        return App.instance;
    }
    public static float dp2px(float dp) {
        // 换种写法，都一样。
//        float density = getContext().getResources().getDisplayMetrics().density;
//        return dp * density;
        return TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public static int dp2pxInt(float dp) {
        return (int) dp2px(dp);
    }

    public static float px2dp(float px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    public static int parseInt(String str, int defaultValue) {
        if (str == null || TextUtils.isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int parseInt(String str) {
        return parseInt(str, 0);
    }
}
