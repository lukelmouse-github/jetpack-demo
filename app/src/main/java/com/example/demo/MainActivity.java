package com.example.demo;

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
    }
}