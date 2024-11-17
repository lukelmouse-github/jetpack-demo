package com.example.demo;

import com.drake.logcat.LogCat;
import com.drake.tooltip.ToastKt;
import com.example.common.base.BaseActivity;
import com.example.common.routers.RouterPath;
import com.example.common.routers.Router;
import com.example.demo.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void initData() {
        super.initData();
        Router.switchFragment(RouterPath.STUDY_MAIN);
    }

    @Override
    protected void initView() {
        super.initView();

        binding.btnView.setOnClickListener(v -> {
            Router.openFragment(RouterPath.VIEW);
        });

        binding.btnAPI.setOnClickListener(v -> {
            Router.openFragment(RouterPath.API);
        });

        binding.btnStudy.setOnClickListener(v -> {
            Router.openFragment(RouterPath.STUDY_MAIN);
        });
    }
}