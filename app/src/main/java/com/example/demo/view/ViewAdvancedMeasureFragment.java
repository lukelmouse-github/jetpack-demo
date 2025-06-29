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
import com.example.demo.databinding.FragmentViewAdvancedMeasureBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.VIEW_ADVANCED_MEASURE, description = "View高级测量演示")
public class ViewAdvancedMeasureFragment extends BaseFragment<FragmentViewAdvancedMeasureBinding> {
    private static final String TAG = "ViewAdvancedMeasureFragment";

    private AdvancedMeasureDemo mAdvancedMeasureDemo;

    public ViewAdvancedMeasureFragment() {
        super(R.layout.fragment_view_advanced_measure);
    }

    @Override
    protected void initView() {
        super.initView();

        ALog.dd(TAG, "ViewAdvancedMeasureFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("ViewGroup高级测量演示");
        titleView.setTextSize(20);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(20, 20, 20, 10);
        binding.main.addView(titleView);

        // 添加说明文本
        TextView descView = new TextView(getContext());
        descView.setText("学习要点：\n" +
                "• ViewGroup如何测量多个子View\n" +
                "• MeasureSpec的创建和传递过程\n" +
                "• 不同测量策略的子View处理\n" +
                "• 父View如何根据子View计算自身尺寸\n" +
                "• 多轮测量的触发条件\n\n" +
                "包含：固定尺寸View (红)、包裹内容View (绿)、填充父View (蓝)");
        descView.setTextSize(14);
        descView.setTextColor(Color.GRAY);
        descView.setPadding(20, 0, 20, 20);
        binding.main.addView(descView);

        // 添加高级测量演示
        mAdvancedMeasureDemo = new AdvancedMeasureDemo(getContext());
        LinearLayout.LayoutParams advancedParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        advancedParams.setMargins(20, 10, 20, 10);
        binding.main.addView(mAdvancedMeasureDemo, advancedParams);

        // 添加重新测量按钮
        Button remeasureButton = new Button(getContext());
        remeasureButton.setText("触发重新测量");
        remeasureButton.setOnClickListener(v -> {
            ALog.dd(TAG, "触发重新测量按钮被点击");
            mAdvancedMeasureDemo.resetMeasureCount();
            mAdvancedMeasureDemo.requestLayout();
            ALog.dd(TAG, "已调用requestLayout()触发重新测量");
        });
        LinearLayout.LayoutParams remeasureButtonParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        remeasureButtonParams.setMargins(20, 10, 20, 10);
        binding.main.addView(remeasureButton, remeasureButtonParams);

        // 添加requestLayout vs invalidate演示
        RequestLayoutInvalidateDemo requestLayoutInvalidateDemo = new RequestLayoutInvalidateDemo(getContext());
        LinearLayout.LayoutParams demoParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        demoParams.setMargins(20, 20, 20, 10);
        binding.main.addView(requestLayoutInvalidateDemo, demoParams);

        // 添加控制按钮布局
        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button requestLayoutBtn = new Button(getContext());
        requestLayoutBtn.setText("测试requestLayout");
        requestLayoutBtn.setOnClickListener(v -> {
            ALog.dd(TAG, "测试requestLayout被点击");
            requestLayoutInvalidateDemo.testRequestLayout();
        });

        Button invalidateBtn = new Button(getContext());
        invalidateBtn.setText("测试invalidate");
        invalidateBtn.setOnClickListener(v -> {
            ALog.dd(TAG, "测试invalidate被点击");
            requestLayoutInvalidateDemo.testInvalidate();
        });

        Button resetBtn = new Button(getContext());
        resetBtn.setText("重置计数");
        resetBtn.setOnClickListener(v -> {
            ALog.dd(TAG, "重置计数被点击");
            requestLayoutInvalidateDemo.resetCounters();
        });

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        );
        btnParams.setMargins(5, 0, 5, 0);

        buttonLayout.addView(requestLayoutBtn, btnParams);
        buttonLayout.addView(invalidateBtn, btnParams);
        buttonLayout.addView(resetBtn, btnParams);

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonLayoutParams.setMargins(20, 10, 20, 10);
        binding.main.addView(buttonLayout, buttonLayoutParams);

        // 添加requestLayout vs invalidate底层原理说明
        TextView updateMechanismInfo = new TextView(getContext());
        updateMechanismInfo.setText("🔄 View更新机制详解：\n\n" +
                "📋 requestLayout()：\n" +
                "• 触发完整流程：measure → layout → draw\n" +
                "• 使用场景：尺寸、位置、布局参数改变\n" +
                "• 性能开销：高（三个阶段都执行）\n" +
                "• 底层原理：设置FORCE_LAYOUT标志向上递归\n\n" +

                "🎨 invalidate()：\n" +
                "• 只触发绘制流程：draw\n" +
                "• 使用场景：颜色、文字、背景改变\n" +
                "• 性能开销：低（只执行绘制阶段）\n" +
                "• 底层原理：设置DIRTY标志，标记重绘区域\n\n" +

                "⚡ 性能对比：\n" +
                "invalidate() 比 requestLayout() 快约 60-80%\n\n" +

                "🧪 实验说明：\n" +
                "• 点击按钮观察不同方法触发的回调次数\n" +
                "• requestLayout会触发onMeasure + onLayout + onDraw\n" +
                "• invalidate只会触发onDraw");
        updateMechanismInfo.setTextSize(13);
        updateMechanismInfo.setTextColor(Color.DKGRAY);
        updateMechanismInfo.setPadding(20, 10, 20, 10);
        updateMechanismInfo.setBackgroundColor(Color.parseColor("#FFF3E0")); // 浅橙色背景
        LinearLayout.LayoutParams updateInfoParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        updateInfoParams.setMargins(20, 10, 20, 10);
        binding.main.addView(updateMechanismInfo, updateInfoParams);

        // 添加MeasureSpec说明
        TextView measureSpecInfo = new TextView(getContext());
        measureSpecInfo.setText("📐 MeasureSpec三种模式：\n" +
                "• EXACTLY: 精确尺寸 (match_parent, 固定dp)\n" +
                "• AT_MOST: 最大限制 (wrap_content)\n" +
                "• UNSPECIFIED: 无限制 (ScrollView内部子View)\n\n" +
                "📊 测量流程：\n" +
                "1. 父ViewGroup创建子View的MeasureSpec\n" +
                "2. 调用子View.measure()传递约束\n" +
                "3. 子View在onMeasure()中计算尺寸\n" +
                "4. 父ViewGroup收集测量结果\n" +
                "5. 父ViewGroup计算自身尺寸");
        measureSpecInfo.setTextSize(13);
        measureSpecInfo.setTextColor(Color.DKGRAY);
        measureSpecInfo.setPadding(20, 10, 20, 10);
        measureSpecInfo.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams specParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        specParams.setMargins(20, 20, 20, 10);
        binding.main.addView(measureSpecInfo, specParams);

        // 添加子View类型说明
        TextView childInfo = new TextView(getContext());
        childInfo.setText("🎯 子View测量策略：\n" +
                "🔴 红色View: 固定100x50，忽略父View约束\n" +
                "🟢 绿色View: 根据父View约束计算最佳尺寸\n" +
                "🔵 蓝色View: 宽度填充父View，高度固定60dp");
        childInfo.setTextSize(13);
        childInfo.setTextColor(Color.DKGRAY);
        childInfo.setPadding(20, 10, 20, 10);
        childInfo.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        childParams.setMargins(20, 10, 20, 10);
        binding.main.addView(childInfo, childParams);

        // 添加日志提示
        TextView logTip = new TextView(getContext());
        logTip.setText("💡 调试技巧：使用 adb logcat | grep \"AdvancedMeasure\" 查看详细日志");
        logTip.setTextSize(12);
        logTip.setTextColor(Color.BLUE);
        logTip.setPadding(20, 10, 20, 10);
        binding.main.addView(logTip);

        ALog.dd(TAG, "ViewAdvancedMeasureFragment initView完成");
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "ViewAdvancedMeasureFragment initData");
    }

    @Override
    public void onResume() {
        super.onResume();
        ALog.dd(TAG, "ViewAdvancedMeasureFragment onResume");
    }
}