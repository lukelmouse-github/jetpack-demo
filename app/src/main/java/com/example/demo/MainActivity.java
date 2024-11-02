package com.example.demo;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.common.base.BaseActivity;
import com.example.common.routers.RouterPath;
import com.example.demo.databinding.ActivityMainBinding;
import com.therouter.TheRouter;

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

        binding.button.setOnClickListener(v -> {
            Fragment a = TheRouter.build(RouterPath.VIEW).createFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, a)
                    .addToBackStack(null)
                    .commit();
        });
    }
}