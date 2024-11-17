package com.example.demo;

import com.drake.logcat.LogCat;
import com.example.common.base.BaseActivity;
import com.example.common.routers.RouterPath;
import com.example.common.routers.Router;
import com.example.demo.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void initData() {
        super.initData();

        try {

            Process process = Runtime.getRuntime().exec("adb logcat -d");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            for (int i = 0; i < 100; i++) {
                line = reader.readLine();
                if (line != null) {
                    log.append(line).append("\n");
                }
            }
            reader.close();
            LogCat.d(log.toString());
        } catch (IOException e) {
            LogCat.d(e.getMessage());
        }
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