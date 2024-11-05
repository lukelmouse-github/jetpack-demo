package com.example.common.utils;

import android.content.Context;
import android.text.TextUtils;

import com.example.common.base.App;

public class UIUtils {

    public static Context getContext() {
        return App.instance;
    }
    public static float dp2px(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return dp * density;
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
