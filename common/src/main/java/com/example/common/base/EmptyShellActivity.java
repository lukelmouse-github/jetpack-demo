package com.example.common.base;

import com.drake.logcat.LogCat;
import com.example.common.R;
import com.example.common.databinding.ActivityEmptyShellBinding;
import com.example.common.routers.RouterPath;
import com.therouter.router.Autowired;
import com.therouter.router.Route;

@Route(path = RouterPath.EMPTY_SHELL_ACTIVITY, description = "Fragment容器页")
public class EmptyShellActivity extends BaseActivity<ActivityEmptyShellBinding> {

    @Autowired // 用这个，不能是private。
    BaseFragment fragment;

    public EmptyShellActivity() {
        super(R.layout.activity_empty_shell);
    }

    @Override
    protected void initView() {
        super.initView();
        if (fragment == null) {
            LogCat.e("EmptyShellActivity 的 fragment 为 null 无法跳转");
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss();
    }
}
