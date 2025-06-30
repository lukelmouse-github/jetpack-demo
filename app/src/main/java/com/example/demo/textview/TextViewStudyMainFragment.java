package com.example.demo.textview;

import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.Router;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentTextviewStudyMainBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.TEXTVIEW_STUDY_MAIN, description = "TextView学习主页面")
public class TextViewStudyMainFragment extends BaseFragment<FragmentTextviewStudyMainBinding> {
    private static final String TAG = "TextViewStudyMainFragment";

    public TextViewStudyMainFragment() {
        super(R.layout.fragment_textview_study_main);
    }

    @Override
    protected void initView() {
        super.initView();

        ALog.dd(TAG, "TextViewStudyMainFragment initView开始");

        // 创建主标题
        TextView titleView = new TextView(getContext());
        titleView.setText("📝 Android TextView底层原理深度解析");
        titleView.setTextSize(24);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(0, 0, 0, 20);
        titleView.setGravity(android.view.Gravity.CENTER);
        binding.main.addView(titleView);

        // 添加介绍文本
        TextView introView = new TextView(getContext());
        introView.setText("🎯 深入理解TextView的核心机制\n\n" +
                "通过6个专门的演示模块，全面掌握TextView的文本测量、绘制、hint机制、选择编辑等核心原理。" +
                "每个模块都包含详细的源码分析和交互演示，帮助你从底层理解Android文本显示系统。");
        introView.setTextSize(16);
        introView.setTextColor(Color.DKGRAY);
        introView.setPadding(10, 10, 10, 30);
        introView.setLineSpacing(8, 1.2f);
        binding.main.addView(introView);

        // 创建学习模块按钮
        createStudyButton(
                "1️⃣ 文本测量与布局机制",
                "深入理解TextView的测量流程\n• StaticLayout vs DynamicLayout\n• TextPaint配置与字体测量\n• 多行文本高度计算\n• ellipsize省略号处理",
                RouterPath.TEXTVIEW_MEASURE,
                Color.parseColor("#E3F2FD")
        );

        createStudyButton(
                "2️⃣ 文本绘制与渲染原理",
                "掌握文本绘制的底层实现\n• Canvas.drawText()绘制流程\n• Typeface字体管理机制\n• Spannable富文本渲染\n• 硬件加速优化策略",
                RouterPath.TEXTVIEW_RENDER,
                Color.parseColor("#F3E5F5")
        );

        createStudyButton(
                "3️⃣ Hint机制深度解析 ⭐",
                "全面理解hint显示原理\n• hint显示时机判断逻辑\n• hint绘制位置和样式控制\n• hint与焦点交互机制\n• Material Design浮动动画\n• 多行hint处理策略",
                RouterPath.TEXTVIEW_HINT,
                Color.parseColor("#FFE8E8")
        );

        createStudyButton(
                "4️⃣ 文本选择与编辑机制",
                "深入文本交互处理\n• SelectionController选择控制\n• 光标绘制和闪烁动画\n• 复制粘贴底层实现\n• InputConnection输入法交互",
                RouterPath.TEXTVIEW_SELECTION,
                Color.parseColor("#E8F5E8")
        );

        createStudyButton(
                "5️⃣ 性能优化策略",
                "掌握TextView性能优化\n• TextLayoutCache缓存机制\n• StaticLayout重用策略\n• 大文本虚拟化处理\n• BufferType选择影响\n• hint缓存优化技巧",
                RouterPath.TEXTVIEW_PERFORMANCE,
                Color.parseColor("#FFF3E0")
        );

        createStudyButton(
                "6️⃣ 高级特性与扩展",
                "探索TextView高级功能\n• AutoSizeTextType自动调整\n• Linkify链接识别处理\n• Movement文本移动机制\n• TransformationMethod变换\n• 国际化和RTL支持",
                RouterPath.TEXTVIEW_ADVANCED,
                Color.parseColor("#F0F8FF")
        );

        // 添加调试提示
        TextView debugTip = new TextView(getContext());
        debugTip.setText("🛠️ 调试技巧\n\n" +
                "建议使用以下命令过滤日志，观察TextView的详细执行流程：\n\n" +
                "adb logcat | grep \"TextViewMeasure\\|TextViewRender\\|TextViewHint\\|TextViewSelection\\|TextViewPerf\"\n\n" +
                "特别关注hint相关的日志输出，了解hint显示和隐藏的完整生命周期。");
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

        ALog.dd(TAG, "TextViewStudyMainFragment initView完成");
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
            ALog.dd(TAG, "跳转到TextView学习模块: " + routePath);
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
        ALog.dd(TAG, "TextViewStudyMainFragment initData");
    }

    @Override
    public void onResume() {
        super.onResume();
        ALog.dd(TAG, "TextViewStudyMainFragment onResume");
    }
}