package com.example.demo.view;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentViewLifecycleBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.VIEW_LIFECYCLE, description = "View生命周期演示")
public class ViewLifecycleFragment extends BaseFragment<FragmentViewLifecycleBinding> {
    private static final String TAG = "ViewLifecycleFragment";

    private ViewLifecycleDemo mViewLifecycleDemo;

    public ViewLifecycleFragment() {
        super(R.layout.fragment_view_lifecycle);
    }

    @Override
    protected void initView() {
        super.initView();

        ALog.dd(TAG, "ViewLifecycleFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("View生命周期演示");
        titleView.setTextSize(20);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(20, 20, 20, 10);
        binding.main.addView(titleView);

        // 添加说明文本
        TextView descView = new TextView(getContext());
        descView.setText("学习要点：\n" +
                "• onAttachedToWindow/onDetachedFromWindow: View添加/移除窗口\n" +
                "• onVisibilityChanged: 可见性变化回调\n" +
                "• onSizeChanged: 尺寸变化回调\n" +
                "• onFocusChanged: 焦点变化回调\n" +
                "• onFinishInflate: 从XML加载完成\n\n" +
                "操作：切换可见性观察生命周期回调");
        descView.setTextSize(14);
        descView.setTextColor(Color.GRAY);
        descView.setPadding(20, 0, 20, 20);
        binding.main.addView(descView);

        // 添加生命周期演示View
        mViewLifecycleDemo = new ViewLifecycleDemo(getContext());
        LinearLayout.LayoutParams lifecycleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lifecycleParams.setMargins(20, 10, 20, 10);
        binding.main.addView(mViewLifecycleDemo, lifecycleParams);

        // 添加可见性控制按钮
        Button visibilityButton = new Button(getContext());
        visibilityButton.setText("切换View可见性");
        visibilityButton.setOnClickListener(v -> {
            if (mViewLifecycleDemo.getVisibility() == View.VISIBLE) {
                mViewLifecycleDemo.setVisibility(View.INVISIBLE);
                ALog.dd(TAG, "设置ViewLifecycleDemo为INVISIBLE");
                visibilityButton.setText("显示View (当前:INVISIBLE)");
            } else {
                mViewLifecycleDemo.setVisibility(View.VISIBLE);
                ALog.dd(TAG, "设置ViewLifecycleDemo为VISIBLE");
                visibilityButton.setText("隐藏View (当前:VISIBLE)");
            }
        });
        LinearLayout.LayoutParams visibilityButtonParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        visibilityButtonParams.setMargins(20, 10, 20, 10);
        binding.main.addView(visibilityButton, visibilityButtonParams);

        // 添加GONE按钮
        Button goneButton = new Button(getContext());
        goneButton.setText("设置为GONE");
        goneButton.setOnClickListener(v -> {
            if (mViewLifecycleDemo.getVisibility() == View.GONE) {
                mViewLifecycleDemo.setVisibility(View.VISIBLE);
                ALog.dd(TAG, "设置ViewLifecycleDemo为VISIBLE");
                goneButton.setText("设置为GONE");
            } else {
                mViewLifecycleDemo.setVisibility(View.GONE);
                ALog.dd(TAG, "设置ViewLifecycleDemo为GONE");
                goneButton.setText("显示View (当前:GONE)");
            }
        });
        LinearLayout.LayoutParams goneButtonParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        goneButtonParams.setMargins(20, 10, 20, 10);
        binding.main.addView(goneButton, goneButtonParams);

        // 添加生命周期说明
        TextView lifecycleInfo = new TextView(getContext());
        lifecycleInfo.setText("🔄 生命周期回调：\n" +
                "• 构造方法: View创建\n" +
                "• onFinishInflate: XML解析完成\n" +
                "• onAttachedToWindow: 添加到窗口\n" +
                "• onMeasure -> onLayout -> onDraw: 显示过程\n" +
                "• onVisibilityChanged: 可见性变化\n" +
                "• onSizeChanged: 尺寸变化\n" +
                "• onFocusChanged: 焦点变化\n" +
                "• onDetachedFromWindow: 从窗口移除");
        lifecycleInfo.setTextSize(13);
        lifecycleInfo.setTextColor(Color.DKGRAY);
        lifecycleInfo.setPadding(20, 10, 20, 10);
        lifecycleInfo.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        infoParams.setMargins(20, 20, 20, 10);
        binding.main.addView(lifecycleInfo, infoParams);

        // 添加日志提示
        TextView logTip = new TextView(getContext());
        logTip.setText("💡 调试技巧：使用 adb logcat | grep \"ViewLifecycleDemo\" 查看详细日志");
        logTip.setTextSize(12);
        logTip.setTextColor(Color.BLUE);
        logTip.setPadding(20, 10, 20, 10);
        binding.main.addView(logTip);

        ALog.dd(TAG, "ViewLifecycleFragment initView完成");
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "ViewLifecycleFragment initData");
    }

    @Override
    public void onResume() {
        super.onResume();
        ALog.dd(TAG, "ViewLifecycleFragment onResume");
    }
}

/**
 * TODO 如何在RecyclerView中针对Item单独做一个曝光统计的呢???包括曝光了多少区域,区域内的文字等等等.
 */