package com.example.demo.view;

import android.graphics.Color;

import com.drake.logcat.LogCat;
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

        CustomSlider customSlider = new CustomSlider(getContext());
        customSlider.setMinValue(55.5f);
        customSlider.setMaxValue(95.5f);
        customSlider.setCurrentValue(60.3f);
        customSlider.setDisabled(false);
        customSlider.setBlockSize(28);
        customSlider.setStepSize(2); // 设置步长为1
        customSlider.setShowValue(true); // 显示当前值
        customSlider.setTrackColor(Color.parseColor("#88ffee")); // 设置轨道颜色
        customSlider.setSelectedTrackColor(Color.parseColor("#ff2244")); // 设置已选择轨道颜色
        customSlider.setThumbColor(Color.BLUE); // 设置滑块颜色

        customSlider.setOnValueChangingListener(value -> {
            // 在滑动过程中更新值
            // 可以在这里做一些实时的更新，比如更新 UI
            LogCat.e("当前值Changing: " + value);
        });

        customSlider.setOnValueChangeListener(value -> {
            LogCat.e("当前值Change: " + value);
        });
        binding.main.addView(customSlider);
    }
}
