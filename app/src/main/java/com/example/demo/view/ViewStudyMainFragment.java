package com.example.demo.view;

import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.Router;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentViewStudyMainBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.VIEW_STUDY_MAIN, description = "View学习主页面")
public class ViewStudyMainFragment extends BaseFragment<FragmentViewStudyMainBinding> {
    private static final String TAG = "ViewStudyMainFragment";

    public ViewStudyMainFragment() {
        super(R.layout.fragment_view_study_main);
    }

    @Override
    protected void initView() {
        super.initView();

        ALog.dd(TAG, "ViewStudyMainFragment initView开始");

        // 创建主标题
        TextView titleView = new TextView(getContext());
        titleView.setText("📱 Android View系统学习大全");
        titleView.setTextSize(24);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(0, 0, 0, 20);
        titleView.setGravity(android.view.Gravity.CENTER);
        binding.main.addView(titleView);

        // 添加介绍文本
        TextView introView = new TextView(getContext());
        introView.setText("🎯 全面掌握Android View工作机制\n\n" +
                "通过5个专门的演示模块，深入理解View的测量、布局、绘制和事件分发机制。" +
                "每个模块都包含详细的日志输出和交互演示，帮助你从源码层面理解Android View系统。");
        introView.setTextSize(16);
        introView.setTextColor(Color.DKGRAY);
        introView.setPadding(10, 10, 10, 30);
        introView.setLineSpacing(8, 1.2f);
        binding.main.addView(introView);

        // 创建学习模块按钮
        createStudyButton(
                "1️⃣ View基础：测量·布局·绘制",
                "学习View的核心工作流程\n• onMeasure() MeasureSpec解析\n• onLayout() 位置计算\n• onDraw() Canvas绘制\n• onTouchEvent() 触摸处理",
                RouterPath.VIEW_BASIC_MEASURE,
                Color.parseColor("#E3F2FD")
        );

        createStudyButton(
                "2️⃣ 触摸事件分发机制",
                "掌握事件分发的完整流程\n• dispatchTouchEvent() 分发入口\n• onInterceptTouchEvent() 拦截机制\n• 父子View事件传递链路\n• 事件消费与拦截的区别",
                RouterPath.VIEW_EVENT_DISPATCH,
                Color.parseColor("#F3E5F5")
        );

        createStudyButton(
                "3️⃣ View生命周期管理",
                "了解View的完整生命周期\n• attach/detach 窗口管理\n• 可见性变化回调\n• 尺寸变化监听\n• 焦点状态管理",
                RouterPath.VIEW_LIFECYCLE,
                Color.parseColor("#E8F5E8")
        );

        createStudyButton(
                "4️⃣ ViewGroup高级测量",
                "深入ViewGroup测量机制\n• 多子View测量策略\n• MeasureSpec创建与传递\n• 不同布局模式处理\n• 多轮测量优化",
                RouterPath.VIEW_ADVANCED_MEASURE,
                Color.parseColor("#FFF3E0")
        );

        // 添加调试提示
        TextView debugTip = new TextView(getContext());
        debugTip.setText("🛠️ 调试技巧\n\n" +
                "建议使用以下命令过滤日志，观察详细的执行流程：\n\n" +
                "adb logcat | grep \"CustomDemoView\\|TouchEvent\\|ViewLifecycle\\|AdvancedMeasure\\|NestedTouch\"\n\n" +
                "每个演示都包含丰富的ALog日志输出，帮助你理解View系统的内部工作原理。");
        debugTip.setTextSize(14);
        debugTip.setTextColor(Color.BLUE);
        debugTip.setPadding(20, 30, 20, 20);
        debugTip.setBackgroundColor(Color.parseColor("#E8F4FD"));
        LinearLayout.LayoutParams tipParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tipParams.setMargins(0, 20, 0, 0);
        binding.main.addView(debugTip, tipParams);

        ALog.dd(TAG, "ViewStudyMainFragment initView完成");
    }

    private void createStudyButton(String title, String description, String routePath, int backgroundColor) {
        // 创建容器
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(20, 15, 20, 15);
        container.setBackgroundColor(backgroundColor);

        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(0, 0, 0, 15);
        container.setLayoutParams(containerParams);

        // 标题
        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setTextSize(18);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(0, 0, 0, 8);
        container.addView(titleView);

        // 描述
        TextView descView = new TextView(getContext());
        descView.setText(description);
        descView.setTextSize(14);
        descView.setTextColor(Color.GRAY);
        descView.setPadding(0, 0, 0, 15);
        descView.setLineSpacing(4, 1.1f);
        container.addView(descView);

        // 按钮
        Button button = new Button(getContext());
        button.setText("开始学习 →");
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.parseColor("#2196F3"));
        button.setPadding(30, 10, 30, 10);
        button.setOnClickListener(v -> {
            ALog.dd(TAG, "跳转到: " + routePath);
            Router.switchFragment(routePath);
        });

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = android.view.Gravity.CENTER;
        button.setLayoutParams(buttonParams);
        container.addView(button);

        binding.main.addView(container);
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "ViewStudyMainFragment initData");
    }

    @Override
    public void onResume() {
        super.onResume();
        ALog.dd(TAG, "ViewStudyMainFragment onResume");
    }
}