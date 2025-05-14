package com.example.demo;

import android.os.Bundle;
import android.util.Log;

import com.example.common.base.BaseActivity;
import com.example.common.routers.Router;
import com.example.common.routers.RouterPath;
import com.example.demo.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置CustomEditText的监听器
        binding.customEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "Text changed: " + s);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();

        Router.openFragment(RouterPath.STUDY_MAIN_LAYOUT);

        binding.btnView.setOnClickListener(v -> {
            Router.openFragment(RouterPath.STUDY_MAIN_LAYOUT);
        });
    }
}