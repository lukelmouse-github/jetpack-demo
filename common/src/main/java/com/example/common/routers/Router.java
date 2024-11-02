package com.example.common.routers;

import androidx.fragment.app.Fragment;
import com.therouter.TheRouter;

public class Router {
    public static void openFragment(String path) {
        Fragment fg = TheRouter.build(path).createFragment();
        TheRouter.build(RouterPath.EMPTY_SHELL_ACTIVITY)
                .withObject("fragment", fg)
                .navigation();
    }
}
