package com.example.demo.textview;

import android.graphics.Color;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentTextviewSelectionBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.TEXTVIEW_SELECTION, description = "TextView文本选择与编辑机制")
public class TextViewSelectionFragment extends BaseFragment<FragmentTextviewSelectionBinding> {
    private static final String TAG = "TextViewSelection";

    public TextViewSelectionFragment() {
        super(R.layout.fragment_textview_selection);
    }

    @Override
    protected void initView() {
        super.initView();
        ALog.dd(TAG, "TextViewSelectionFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("✂️ TextView文本选择与编辑机制");
        titleView.setTextSize(22);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(0, 0, 0, 20);
        titleView.setGravity(android.view.Gravity.CENTER);
        binding.main.addView(titleView);

        addTheorySection();
        addSelectionDemo();
        addCursorDemo();
        addInputDemo();

        ALog.dd(TAG, "TextViewSelectionFragment initView完成");
    }

    private void addTheorySection() {
        TextView theoryTitle = new TextView(getContext());
        theoryTitle.setText("📚 文本编辑理论基础");
        theoryTitle.setTextSize(18);
        theoryTitle.setTextColor(Color.BLACK);
        theoryTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(theoryTitle);

        TextView theoryContent = new TextView(getContext());
        theoryContent.setText(
                "1️⃣ SelectionController选择控制：\n" +
                "   • 文本选择范围管理\n" +
                "   • 选择手柄的绘制和交互\n" +
                "   • 长按选择的触发机制\n\n" +

                "2️⃣ 光标绘制和动画：\n" +
                "   • Cursor闪烁动画实现\n" +
                "   • 光标位置计算\n" +
                "   • 输入焦点管理\n\n" +

                "3️⃣ 复制粘贴机制：\n" +
                "   • ClipboardManager接口\n" +
                "   • ActionMode上下文菜单\n" +
                "   • 系统剪贴板交互"
        );
        theoryContent.setTextSize(14);
        theoryContent.setTextColor(Color.DKGRAY);
        theoryContent.setPadding(10, 0, 10, 20);
        theoryContent.setLineSpacing(6, 1.2f);
        theoryContent.setBackgroundColor(Color.parseColor("#F8F9FA"));
        binding.main.addView(theoryContent);
    }

    private void addSelectionDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🎯 文本选择演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示文本选择控制器和选择手柄交互...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#E3F2FD"));
        binding.main.addView(demoText);
    }

    private void addCursorDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("📍 光标动画演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示光标闪烁动画和位置计算...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#F3E5F5"));
        binding.main.addView(demoText);
    }

    private void addInputDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("⌨️ 输入法交互演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        TextView demoText = new TextView(getContext());
        demoText.setText("演示InputConnection和输入法的交互机制...");
        demoText.setTextSize(16);
        demoText.setTextColor(Color.DKGRAY);
        demoText.setPadding(10, 10, 10, 20);
        demoText.setBackgroundColor(Color.parseColor("#E8F5E8"));
        binding.main.addView(demoText);
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "TextViewSelectionFragment initData");
    }
}