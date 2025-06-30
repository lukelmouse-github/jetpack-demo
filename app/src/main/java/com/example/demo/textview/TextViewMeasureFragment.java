package com.example.demo.textview;

import android.graphics.Color;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentTextviewMeasureBinding;
import com.therouter.router.Route;

// TODO: 在RecyclerView学习事件分发逻辑.
@Route(path = RouterPath.TEXTVIEW_MEASURE, description = "TextView文本测量与布局机制")
public class TextViewMeasureFragment extends BaseFragment<FragmentTextviewMeasureBinding> {
    private static final String TAG = "TextViewMeasure";

    public TextViewMeasureFragment() {
        super(R.layout.fragment_textview_measure);
    }

    @Override
    protected void initView() {
        super.initView();
        ALog.dd(TAG, "TextViewMeasureFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("📏 TextView测量与布局机制深度解析");
        titleView.setTextSize(22);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(0, 0, 0, 20);
        titleView.setGravity(android.view.Gravity.CENTER);
        binding.main.addView(titleView);

        addTheorySection();
        addStaticLayoutDemo();
        addTextPaintDemo();
        addMeasureSpecDemo();

        ALog.dd(TAG, "TextViewMeasureFragment initView完成");
    }

    private void addTheorySection() {
        TextView theoryTitle = new TextView(getContext());
        theoryTitle.setText("📚 文本测量理论基础");
        theoryTitle.setTextSize(18);
        theoryTitle.setTextColor(Color.BLACK);
        theoryTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(theoryTitle);

        TextView theoryContent = new TextView(getContext());
        theoryContent.setText(
                "1️⃣ StaticLayout高性能布局：\n" +
                "   • 静态文本布局，支持复杂排版\n" +
                "   • 多行文本的高效测量和绘制\n" +
                "   • 缓存机制，避免重复计算\n\n" +

                "2️⃣ TextPaint绘制配置：\n" +
                "   • 字体、大小、颜色设置\n" +
                "   • 文本宽度和高度测量\n" +
                "   • measureText()精确计算\n\n" +

                "3️⃣ MeasureSpec约束传递：\n" +
                "   • EXACTLY：固定尺寸\n" +
                "   • AT_MOST：最大尺寸约束\n" +
                "   • UNSPECIFIED：无约束测量"
        );
        theoryContent.setTextSize(14);
        theoryContent.setTextColor(Color.DKGRAY);
        theoryContent.setPadding(10, 0, 10, 20);
        theoryContent.setLineSpacing(6, 1.2f);
        theoryContent.setBackgroundColor(Color.parseColor("#F8F9FA"));
        binding.main.addView(theoryContent);
    }

    private void addStaticLayoutDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🏗️ StaticLayout演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示StaticLayout的测量和布局功能...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#E3F2FD"));
        binding.main.addView(demoText);
    }

    private void addTextPaintDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🎨 TextPaint演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示TextPaint的文本测量功能...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#F3E5F5"));
        binding.main.addView(demoText);
    }

    private void addMeasureSpecDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("📐 MeasureSpec演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示MeasureSpec在文本测量中的应用...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#E8F5E8"));
        binding.main.addView(demoText);
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "TextViewMeasureFragment initData");
    }
}