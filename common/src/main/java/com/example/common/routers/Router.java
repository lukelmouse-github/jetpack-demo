package com.example.common.routers;

import androidx.fragment.app.Fragment;

import com.drake.logcat.LogCat;
import com.drake.tooltip.ToastKt;
import com.therouter.TheRouter;

public class Router {
    public static void openFragment(String path) {
        Fragment fg = TheRouter.build(path).createFragment();
        TheRouter.build(RouterPath.EMPTY_SHELL_ACTIVITY)
                .withObject("fragment", fg)
                .navigation();
    }

    public static void switchFragment(String path) {
        // 自动切换到学习界面。
        ToastKt.toast("自动切换到 " + path + " 界面");
        LogCat.d("自动切换到 " + path + " 界面");
        openFragment(path);
    }
}
