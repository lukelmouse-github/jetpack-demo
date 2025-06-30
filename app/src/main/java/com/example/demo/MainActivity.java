package com.example.demo;

import com.example.common.base.BaseActivity;
import com.example.common.routers.Router;
import com.example.common.routers.RouterPath;
import com.example.demo.databinding.ActivityMainBinding;
import com.example.demo.route.RoutesAdapter;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";

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
        binding.rvRoutes.setAdapter(new RoutesAdapter(this));

        Router.switchFragment(RouterPath.TEXTVIEW_STUDY_MAIN);
    }
}