package com.example.demo.view;

import com.drake.tooltip.ToastKt;
import com.example.common.base.BaseFragment;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentViewBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.VIEW, description = "自定义View页面")
public class ViewFragment extends BaseFragment<FragmentViewBinding> {
    public ViewFragment() {
        super(R.layout.fragment_view);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.button.setOnClickListener(v -> ToastKt.toast("点击了按钮"));
    }
}
