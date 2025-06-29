package com.example.demo.view;

import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drake.tooltip.ToastKt;
import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentViewEventDispatchBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.VIEW_EVENT_DISPATCH, description = "触摸事件分发演示")
public class ViewEventDispatchFragment extends BaseFragment<FragmentViewEventDispatchBinding> {
    private static final String TAG = "ViewEventDispatchFragment";

    private AViewGroup mAViewGroup;

    public ViewEventDispatchFragment() {
        super(R.layout.fragment_view_event_dispatch);
    }

    @Override
    protected void initView() {
        super.initView();

        ALog.dd(TAG, "ViewEventDispatchFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("三层View事件分发演示");
        titleView.setTextSize(20);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(20, 20, 20, 10);
        binding.main.addView(titleView);

        // 添加说明文本
        TextView descView = new TextView(getContext());
        descView.setText("层次结构：\n" +
                "• A-ViewGroup (蓝色边框) - 最外层\n" +
                "• B1/B2-ViewGroup (紫色边框) - 中间层\n" +
                "• C11/C12/C21/C22-View (橙色边框) - 最内层\n\n" +
                "操作指南：\n" +
                "• 点击任意子View观察完整事件分发流程\n" +
                "• 使用按钮控制各层的事件拦截状态");
        descView.setTextSize(14);
        descView.setTextColor(Color.GRAY);
        descView.setPadding(20, 0, 20, 20);
        binding.main.addView(descView);

        // 创建三层View结构的触摸事件分发演示
        mAViewGroup = new AViewGroup(getContext());
        LinearLayout.LayoutParams aViewParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 500
        );
        aViewParams.setMargins(20, 10, 20, 10);
        binding.main.addView(mAViewGroup, aViewParams);

        // 添加控制按钮区域
        addControlButtons();

        // 添加事件流程说明
        TextView flowView = new TextView(getContext());
        flowView.setText("🔄 三层事件流程：\n" +
                "1. A-ViewGroup.dispatchTouchEvent()\n" +
                "2. A-ViewGroup.onInterceptTouchEvent()\n" +
                "3. B-ViewGroup.dispatchTouchEvent() (如果A不拦截)\n" +
                "4. B-ViewGroup.onInterceptTouchEvent()\n" +
                "5. C-View.onTouchEvent() (如果B不拦截)\n" +
                "6. 返回路径：C->B->A (如果不处理)");
        flowView.setTextSize(13);
        flowView.setTextColor(Color.DKGRAY);
        flowView.setPadding(20, 10, 20, 10);
        flowView.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams flowParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        flowParams.setMargins(20, 20, 20, 10);
        binding.main.addView(flowView, flowParams);

        // 添加源码解析说明
        TextView sourceView = new TextView(getContext());
        sourceView.setText("📖 Android源码解析：\n" +
                "ViewGroup.dispatchTouchEvent()中的关键代码：\n" +
                "• for (int i = childrenCount - 1; i >= 0; i--) // 从后往前遍历\n" +
                "• isTransformedTouchPointInView(x, y, child, null) // 坐标匹配\n" +
                "• dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign) // 分发事件\n\n" +
                "坐标匹配算法在isTransformedTouchPointInView()方法中实现");
        sourceView.setTextSize(12);
        sourceView.setTextColor(Color.parseColor("#2E8B57"));
        sourceView.setPadding(20, 10, 20, 10);
        sourceView.setBackgroundColor(Color.parseColor("#F0FFF0"));
        LinearLayout.LayoutParams sourceParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        sourceParams.setMargins(20, 10, 20, 10);
        binding.main.addView(sourceView, sourceParams);

        // 添加完整链路说明
        TextView chainView = new TextView(getContext());
        chainView.setText("🔥 完整事件传递链路：\n" +
                "硬件层：手指触摸 → 电容变化 → 硬件中断\n" +
                "内核层：驱动程序 → /dev/input/eventX → Input子系统\n" +
                "Native层：InputReader → InputDispatcher → Binder IPC\n" +
                "Framework层：WindowManager → ViewRootImpl → MotionEvent\n" +
                "应用层：Activity → Window → ViewGroup → View ← 我们的demo\n\n" +
                "⏱️ 整个过程通常在10-16ms内完成（一帧时间）");
        chainView.setTextSize(12);
        chainView.setTextColor(Color.parseColor("#8B4513"));
        chainView.setPadding(20, 10, 20, 10);
        chainView.setBackgroundColor(Color.parseColor("#FFF8DC"));
        LinearLayout.LayoutParams chainParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        chainParams.setMargins(20, 10, 20, 10);
        binding.main.addView(chainView, chainParams);

        // 添加日志提示
        TextView logTip = new TextView(getContext());
        logTip.setText("💡 调试技巧：使用 adb logcat | grep \"TouchEvent\" 查看详细日志");
        logTip.setTextSize(12);
        logTip.setTextColor(Color.BLUE);
        logTip.setPadding(20, 10, 20, 10);
        binding.main.addView(logTip);

        ALog.dd(TAG, "ViewEventDispatchFragment initView完成");
    }

    private void addControlButtons() {
        // A-ViewGroup拦截控制按钮
        Button aInterceptButton = new Button(getContext());
        aInterceptButton.setText("切换A-ViewGroup拦截");
        aInterceptButton.setOnClickListener(v -> {
            boolean currentState = mAViewGroup.isInterceptTouchEvent();
            mAViewGroup.setInterceptTouchEvent(!currentState);
            ALog.dd(TAG, "A-ViewGroup拦截状态切换为: " + !currentState);
            ToastKt.toast("A-ViewGroup事件拦截已" + (currentState ? "关闭" : "开启"));
        });
        LinearLayout.LayoutParams aButtonParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        aButtonParams.setMargins(20, 10, 20, 5);
        binding.main.addView(aInterceptButton, aButtonParams);

        // B1-ViewGroup拦截控制按钮
        Button b1InterceptButton = new Button(getContext());
        b1InterceptButton.setText("切换B1-ViewGroup拦截");
        b1InterceptButton.setOnClickListener(v -> {
            boolean currentState = mAViewGroup.getB1ViewGroup().isInterceptTouchEvent();
            mAViewGroup.getB1ViewGroup().setInterceptTouchEvent(!currentState);
            ALog.dd(TAG, "B1-ViewGroup拦截状态切换为: " + !currentState);
            ToastKt.toast("B1-ViewGroup事件拦截已" + (currentState ? "关闭" : "开启"));
        });
        LinearLayout.LayoutParams b1ButtonParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        b1ButtonParams.setMargins(20, 5, 20, 5);
        binding.main.addView(b1InterceptButton, b1ButtonParams);

        // B2-ViewGroup拦截控制按钮
        Button b2InterceptButton = new Button(getContext());
        b2InterceptButton.setText("切换B2-ViewGroup拦截");
        b2InterceptButton.setOnClickListener(v -> {
            boolean currentState = mAViewGroup.getB2ViewGroup().isInterceptTouchEvent();
            mAViewGroup.getB2ViewGroup().setInterceptTouchEvent(!currentState);
            ALog.dd(TAG, "B2-ViewGroup拦截状态切换为: " + !currentState);
            ToastKt.toast("B2-ViewGroup事件拦截已" + (currentState ? "关闭" : "开启"));
        });
        LinearLayout.LayoutParams b2ButtonParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        b2ButtonParams.setMargins(20, 5, 20, 10);
        binding.main.addView(b2InterceptButton, b2ButtonParams);
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "ViewEventDispatchFragment initData");
    }

    @Override
    public void onResume() {
        super.onResume();
        ALog.dd(TAG, "ViewEventDispatchFragment onResume");
    }
}
/**
 * 点击B1空白区域
 * 00:34:45.804D  [TouchEvent-AViewGroup] ========== AViewGroup dispatchTouchEvent ==========
 * 00:34:45.808D  [TouchEvent-AViewGroup] AViewGroup 事件类型: ACTION_DOWN
 * 00:34:45.813D  [TouchEvent-AViewGroup] AViewGroup 坐标: x=493.75, y=138.75
 * 00:34:45.817D  [TouchEvent-AViewGroup] ========== AViewGroup onInterceptTouchEvent ==========
 * 00:34:45.821D  [TouchEvent-AViewGroup] AViewGroup 事件类型: ACTION_DOWN
 * 00:34:45.825D  [TouchEvent-AViewGroup] AViewGroup 当前拦截设置: false
 * 00:34:45.829D  [TouchEvent-AViewGroup] AViewGroup onInterceptTouchEvent 返回: false
 * 00:34:45.833D  [TouchEvent-AViewGroup] ========== AViewGroup onInterceptTouchEvent 结束 ==========
 * 00:34:45.838D  [TouchEvent-BViewGroup] ========== B1-ViewGroup dispatchTouchEvent ==========
 * 00:34:45.841D  [TouchEvent-BViewGroup] B1-ViewGroup 事件类型: ACTION_DOWN
 * 00:34:45.844D  [TouchEvent-BViewGroup] B1-ViewGroup 坐标: x=473.75, y=98.75
 * 00:34:45.847D  [TouchEvent-BViewGroup] ========== B1-ViewGroup onInterceptTouchEvent ==========
 * 00:34:45.850D  [TouchEvent-BViewGroup] B1-ViewGroup 事件类型: ACTION_DOWN
 * 00:34:45.854D  [TouchEvent-BViewGroup] B1-ViewGroup 当前拦截设置: false
 * 00:34:45.857D  [TouchEvent-BViewGroup] B1-ViewGroup onInterceptTouchEvent 返回: false
 * 00:34:45.859D  [TouchEvent-BViewGroup] ========== B1-ViewGroup onInterceptTouchEvent 结束 ==========
 * 00:34:45.862D  [TouchEvent-BViewGroup] ========== B1-ViewGroup onTouchEvent ==========
 * 00:34:45.864D  [TouchEvent-BViewGroup] B1-ViewGroup 事件类型: ACTION_DOWN
 * 00:34:45.867D  [TouchEvent-BViewGroup] B1-ViewGroup 坐标: x=473.75, y=98.75
 * 00:34:45.869D  [TouchEvent-BViewGroup] B1-ViewGroup onTouchEvent 返回: false
 * 00:34:45.871D  [TouchEvent-BViewGroup] ========== B1-ViewGroup onTouchEvent 结束 ==========
 * 00:34:45.873D  [TouchEvent-BViewGroup] B1-ViewGroup dispatchTouchEvent 返回: false
 * 00:34:45.875D  [TouchEvent-BViewGroup] ========== B1-ViewGroup dispatchTouchEvent 结束 ==========
 * 00:34:45.877D  [TouchEvent-AViewGroup] ========== AViewGroup onTouchEvent ==========
 * 00:34:45.879D  [TouchEvent-AViewGroup] AViewGroup 事件类型: ACTION_DOWN
 * 00:34:45.881D  [TouchEvent-AViewGroup] AViewGroup 坐标: x=493.75, y=138.75
 * 00:34:45.883D  [TouchEvent-AViewGroup] AViewGroup onTouchEvent 返回: false
 * 00:34:45.885D  [TouchEvent-AViewGroup] ========== AViewGroup onTouchEvent 结束 ==========
 * 00:34:45.887D  [TouchEvent-AViewGroup] AViewGroup dispatchTouchEvent 返回: false
 * 00:34:45.888D  [TouchEvent-AViewGroup] ========== AViewGroup dispatchTouchEvent 结束 ==========
 *
 *
 */