package com.example.demo.textview;

import android.graphics.Color;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentTextviewRenderBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.TEXTVIEW_RENDER, description = "TextView文本绘制与渲染原理")
public class TextViewRenderFragment extends BaseFragment<FragmentTextviewRenderBinding> {
    private static final String TAG = "TextViewRender";

    public TextViewRenderFragment() {
        super(R.layout.fragment_textview_render);
    }

    @Override
    protected void initView() {
        super.initView();
        ALog.dd(TAG, "TextViewRenderFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("🎨 TextView文本绘制与渲染原理");
        titleView.setTextSize(22);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(0, 0, 0, 20);
        titleView.setGravity(android.view.Gravity.CENTER);
        binding.main.addView(titleView);

        addTheorySection();
        addCanvasDemo();
        addTypefaceDemo();
        addSpannableDemo();

        ALog.dd(TAG, "TextViewRenderFragment initView完成");
    }

    private void addTheorySection() {
        TextView theoryTitle = new TextView(getContext());
        theoryTitle.setText("📚 文本绘制理论基础");
        theoryTitle.setTextSize(18);
        theoryTitle.setTextColor(Color.BLACK);
        theoryTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(theoryTitle);

        TextView theoryContent = new TextView(getContext());
        theoryContent.setText(
                "1️⃣ Canvas.drawText()流程：\n" +
                "   • 文本绘制的底层实现\n" +
                "   • 坐标系统和基线对齐\n" +
                "   • 硬件加速的影响\n\n" +
                
                "2️⃣ Typeface字体管理：\n" +
                "   • 字体文件加载和缓存\n" +
                "   • FontFamily字体族管理\n" +
                "   • 字体回退机制\n\n" +
                
                "3️⃣ Spannable富文本：\n" +
                "   • Span对象的绘制流程\n" +
                "   • 多样式文本混合渲染\n" +
                "   • 性能优化策略"
        );
        theoryContent.setTextSize(14);
        theoryContent.setTextColor(Color.DKGRAY);
        theoryContent.setPadding(10, 0, 10, 20);
        theoryContent.setLineSpacing(6, 1.2f);
        theoryContent.setBackgroundColor(Color.parseColor("#F8F9FA"));
        binding.main.addView(theoryContent);
    }

    private void addCanvasDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🖼️ Canvas绘制演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示Canvas.drawText()的绘制流程和坐标系统...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#E3F2FD"));
        binding.main.addView(demoText);
    }

    private void addTypefaceDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🔤 Typeface演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示字体管理和缓存机制...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#F3E5F5"));
        binding.main.addView(demoText);
    }

    private void addSpannableDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("✨ Spannable富文本演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示富文本的混合渲染和Span对象绘制...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#E8F5E8"));
        binding.main.addView(demoText);
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "TextViewRenderFragment initData");
    }
}