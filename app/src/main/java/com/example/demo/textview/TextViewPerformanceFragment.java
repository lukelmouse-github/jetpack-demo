package com.example.demo.textview;

import android.graphics.Color;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentTextviewPerformanceBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.TEXTVIEW_PERFORMANCE, description = "TextView性能优化策略")
public class TextViewPerformanceFragment extends BaseFragment<FragmentTextviewPerformanceBinding> {
    private static final String TAG = "TextViewPerformance";

    public TextViewPerformanceFragment() {
        super(R.layout.fragment_textview_performance);
    }

    @Override
    protected void initView() {
        super.initView();
        ALog.dd(TAG, "TextViewPerformanceFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("⚡ TextView性能优化策略");
        titleView.setTextSize(22);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(0, 0, 0, 20);
        titleView.setGravity(android.view.Gravity.CENTER);
        binding.main.addView(titleView);

        addTheorySection();
        addCacheDemo();
        addReuseDemo();
        addMemoryDemo();

        ALog.dd(TAG, "TextViewPerformanceFragment initView完成");
    }

    private void addTheorySection() {
        TextView theoryTitle = new TextView(getContext());
        theoryTitle.setText("📚 性能优化理论基础");
        theoryTitle.setTextSize(18);
        theoryTitle.setTextColor(Color.BLACK);
        theoryTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(theoryTitle);

        TextView theoryContent = new TextView(getContext());
        theoryContent.setText(
                "1️⃣ TextLayoutCache缓存机制：\n" +
                "   • 文本布局结果缓存策略\n" +
                "   • LRU缓存过期和清理\n" +
                "   • 缓存命中率优化\n\n" +
                
                "2️⃣ StaticLayout重用策略：\n" +
                "   • 布局对象池管理\n" +
                "   • 避免频繁创建和销毁\n" +
                "   • 内存复用技巧\n\n" +
                
                "3️⃣ 大文本虚拟化：\n" +
                "   • 分页加载和渲染\n" +
                "   • 可视区域优化\n" +
                "   • 懒加载策略"
        );
        theoryContent.setTextSize(14);
        theoryContent.setTextColor(Color.DKGRAY);
        theoryContent.setPadding(10, 0, 10, 20);
        theoryContent.setLineSpacing(6, 1.2f);
        theoryContent.setBackgroundColor(Color.parseColor("#F8F9FA"));
        binding.main.addView(theoryContent);
    }

    private void addCacheDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🗃️ 缓存机制演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示TextLayoutCache的缓存策略和命中率分析...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#E3F2FD"));
        binding.main.addView(demoText);
    }

    private void addReuseDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("♻️ 对象重用演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示StaticLayout对象池和重用策略...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#F3E5F5"));
        binding.main.addView(demoText);
    }

    private void addMemoryDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🧠 内存优化演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示大文本的虚拟化处理和内存管理...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#E8F5E8"));
        binding.main.addView(demoText);
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "TextViewPerformanceFragment initData");
    }
}