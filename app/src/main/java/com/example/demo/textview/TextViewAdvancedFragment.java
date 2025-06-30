package com.example.demo.textview;

import android.graphics.Color;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentTextviewAdvancedBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.TEXTVIEW_ADVANCED, description = "TextView高级特性与扩展")
public class TextViewAdvancedFragment extends BaseFragment<FragmentTextviewAdvancedBinding> {
    private static final String TAG = "TextViewAdvanced";

    public TextViewAdvancedFragment() {
        super(R.layout.fragment_textview_advanced);
    }

    @Override
    protected void initView() {
        super.initView();
        ALog.dd(TAG, "TextViewAdvancedFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("🚀 TextView高级特性与扩展");
        titleView.setTextSize(22);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(0, 0, 0, 20);
        titleView.setGravity(android.view.Gravity.CENTER);
        binding.main.addView(titleView);

        addTheorySection();
        addAutoSizeDemo();
        addLinkifyDemo();
        addTransfromDemo();

        ALog.dd(TAG, "TextViewAdvancedFragment initView完成");
    }

    private void addTheorySection() {
        TextView theoryTitle = new TextView(getContext());
        theoryTitle.setText("📚 高级特性理论基础");
        theoryTitle.setTextSize(18);
        theoryTitle.setTextColor(Color.BLACK);
        theoryTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(theoryTitle);

        TextView theoryContent = new TextView(getContext());
        theoryContent.setText(
                "1️⃣ AutoSizeTextType自动调整：\n" +
                "   • 文字大小自适应容器\n" +
                "   • 二分法查找最优字号\n" +
                "   • 约束条件和边界处理\n\n" +
                
                "2️⃣ Linkify链接识别：\n" +
                "   • 正则模式匹配\n" +
                "   • ClickableSpan点击处理\n" +
                "   • 自定义链接类型\n\n" +
                
                "3️⃣ Movement文本处理：\n" +
                "   • LinkMovementMethod\n" +
                "   • ScrollingMovementMethod\n" +
                "   • 自定义MovementMethod"
        );
        theoryContent.setTextSize(14);
        theoryContent.setTextColor(Color.DKGRAY);
        theoryContent.setPadding(10, 0, 10, 20);
        theoryContent.setLineSpacing(6, 1.2f);
        theoryContent.setBackgroundColor(Color.parseColor("#F8F9FA"));
        binding.main.addView(theoryContent);
    }

    private void addAutoSizeDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("📏 AutoSize演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示文字大小自动调整和约束算法...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#E3F2FD"));
        binding.main.addView(demoText);
    }

    private void addLinkifyDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🔗 Linkify演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示链接识别和点击处理机制...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#F3E5F5"));
        binding.main.addView(demoText);
    }

    private void addTransfromDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🔄 TransfromMethod演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示文本变换和Movement处理...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#E8F5E8"));
        binding.main.addView(demoText);
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "TextViewAdvancedFragment initData");
    }
}