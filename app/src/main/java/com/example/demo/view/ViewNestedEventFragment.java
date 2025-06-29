package com.example.demo.view;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentViewNestedEventBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.VIEW_NESTED_EVENT, description = "多层嵌套触摸事件演示")
public class ViewNestedEventFragment extends BaseFragment<FragmentViewNestedEventBinding> {
    private static final String TAG = "ViewNestedEventFragment";

    private NestedTouchEventDemo mNestedTouchEventDemo;

    public ViewNestedEventFragment() {
        super(R.layout.fragment_view_nested_event);
    }

    @Override
    protected void initView() {
        super.initView();

        ALog.dd(TAG, "ViewNestedEventFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("多层嵌套触摸事件分发演示");
        titleView.setTextSize(20);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(20, 20, 20, 10);
        binding.main.addView(titleView);

        // 添加说明文本
        TextView descView = new TextView(getContext());
        descView.setText("学习要点：\n" +
                "• 多层ViewGroup的事件分发链路\n" +
                "• 中间层事件拦截的影响\n" +
                "• ACTION_CANCEL事件的产生时机\n" +
                "• 复杂嵌套结构中的事件处理\n" +
                "• 事件拦截对子View的影响\n\n" +
                "结构：Parent -> Parent_Child -> Parent_Child_Child -> 子View\n" +
                "特点：第2层(Parent_Child)会拦截DOWN事件");
        descView.setTextSize(14);
        descView.setTextColor(Color.GRAY);
        descView.setPadding(20, 0, 20, 20);
        binding.main.addView(descView);

        // 添加多层嵌套触摸事件演示
        mNestedTouchEventDemo = NestedTouchEventDemo.createDemo(getContext());
        LinearLayout.LayoutParams nestedParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 250
        );
        nestedParams.setMargins(20, 10, 20, 10);
        binding.main.addView(mNestedTouchEventDemo, nestedParams);

        // 添加嵌套结构说明
        TextView structureInfo = new TextView(getContext());
        structureInfo.setText("🏗️ 嵌套结构：\n" +
                "┌─ Parent (Level 1, 浅灰色)\n" +
                "│  └─ Parent_Child (Level 2, 深灰色) 📍拦截点\n" +
                "│     └─ Parent_Child_Child (Level 3, 更深灰色)\n" +
                "│        └─ 子View (青色小块)\n" +
                "\n🎯 事件拦截规则：\n" +
                "• Level 1: 不拦截，正常传递\n" +
                "• Level 2: 拦截DOWN事件，阻止向下传递\n" +
                "• Level 3: 不拦截，但由于Level2拦截而收不到事件");
        structureInfo.setTextSize(13);
        structureInfo.setTextColor(Color.DKGRAY);
        structureInfo.setPadding(20, 10, 20, 10);
        structureInfo.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams structureParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        structureParams.setMargins(20, 20, 20, 10);
        binding.main.addView(structureInfo, structureParams);

        // 添加事件流程说明
        TextView eventFlowInfo = new TextView(getContext());
        eventFlowInfo.setText("🔄 预期事件流程：\n" +
                "1. Parent.dispatchTouchEvent() ✅\n" +
                "2. Parent.onInterceptTouchEvent() ✅ (返回false)\n" +
                "3. Parent_Child.dispatchTouchEvent() ✅\n" +
                "4. Parent_Child.onInterceptTouchEvent() ⚠️ (返回true，拦截!)\n" +
                "5. Parent_Child.onTouchEvent() ✅ (处理事件)\n" +
                "6. Level3和子View收到ACTION_CANCEL ❌\n" +
                "\n💡 观察要点：\n" +
                "• Level2拦截后，Level3不再收到后续事件\n" +
                "• 子View会收到ACTION_CANCEL通知\n" +
                "• 后续MOVE和UP事件直接给Level2处理");
        eventFlowInfo.setTextSize(12);
        eventFlowInfo.setTextColor(Color.DKGRAY);
        eventFlowInfo.setPadding(20, 10, 20, 10);
        eventFlowInfo.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams flowParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        flowParams.setMargins(20, 10, 20, 10);
        binding.main.addView(eventFlowInfo, flowParams);

        // 添加日志提示
        TextView logTip = new TextView(getContext());
        logTip.setText("💡 调试技巧：使用 adb logcat | grep \"NestedTouchDemo\" 查看详细日志");
        logTip.setTextSize(12);
        logTip.setTextColor(Color.BLUE);
        logTip.setPadding(20, 10, 20, 10);
        binding.main.addView(logTip);

        ALog.dd(TAG, "ViewNestedEventFragment initView完成");
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "ViewNestedEventFragment initData");
    }

    @Override
    public void onResume() {
        super.onResume();
        ALog.dd(TAG, "ViewNestedEventFragment onResume");
    }
}