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
import com.example.demo.databinding.FragmentViewBasicMeasureBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.VIEW_BASIC_MEASURE, description = "View基础测量布局绘制演示")
public class ViewBasicMeasureFragment extends BaseFragment<FragmentViewBasicMeasureBinding> {
    private static final String TAG = "ViewBasicMeasureFragment";

    private CustomDemoView mCustomDemoView;

    public ViewBasicMeasureFragment() {
        super(R.layout.fragment_view_basic_measure);
    }

    @Override
    protected void initView() {
        super.initView();

        ALog.dd(TAG, "ViewBasicMeasureFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("View基础测量、布局、绘制演示");
        titleView.setTextSize(20);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(20, 20, 20, 10);
        binding.main.addView(titleView);

        // 添加说明文本
        TextView descView = new TextView(getContext());
        descView.setText("学习要点：\n" +
                "• onMeasure(): 理解MeasureSpec的三种模式\n" +
                "• onLayout(): 观察View如何确定位置和尺寸\n" +
                "• onDraw(): 了解Canvas绘制过程\n" +
                "• onTouchEvent(): 触摸事件处理\n\n" +
                "操作：触摸下方区域查看效果，观察日志输出");
        descView.setTextSize(14);
        descView.setTextColor(Color.GRAY);
        descView.setPadding(20, 0, 20, 20);
        binding.main.addView(descView);

        // 创建自定义View演示测量布局绘制
        mCustomDemoView = new CustomDemoView(getContext());
        mCustomDemoView.setPadding(20, 20, 20, 20);
        LinearLayout.LayoutParams customViewParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        customViewParams.setMargins(20, 10, 20, 10);
        binding.main.addView(mCustomDemoView, customViewParams);

        // 添加重置按钮
        Button resetButton = new Button(getContext());
        resetButton.setText("重置计数器 (触发重新测量)");
        resetButton.setOnClickListener(v -> {
            ALog.dd(TAG, "重置按钮被点击");
            mCustomDemoView.resetCounters();
            // 触发重新测量布局绘制
            mCustomDemoView.requestLayout();
            ALog.dd(TAG, "已调用requestLayout()");
        });
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(20, 10, 20, 10);
        binding.main.addView(resetButton, buttonParams);

        // 添加日志提示
        TextView logTip = new TextView(getContext());
        logTip.setText("💡 调试技巧：使用 adb logcat | grep \"CustomDemoView\" 查看详细日志");
        logTip.setTextSize(12);
        logTip.setTextColor(Color.BLUE);
        logTip.setPadding(20, 20, 20, 10);
        binding.main.addView(logTip);

        ALog.dd(TAG, "ViewBasicMeasureFragment initView完成");
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "ViewBasicMeasureFragment initData");
    }

    @Override
    public void onResume() {
        super.onResume();
        ALog.dd(TAG, "ViewBasicMeasureFragment onResume");
    }
}
/**
 *                                            ========== onMeasure 第1次调用 ==========
 * [CustomDemoView](CustomDemoView.java:60)#onMeasure-[M][Thread:main-2]
 *                      widthMeasureSpec - mode: EXACTLY, size: 896
 * [CustomDemoView](CustomDemoView.java:61)#onMeasure-[M][Thread:main-2]
 *                      heightMeasureSpec - mode: UNSPECIFIED, size: 1436
 *
 *  // 1. 测量过程
 *  MATCH_PARENT,对应精确测量(EXACTLY),直接把父View的宽高传递给子View即可.
 *  WRAP_CONTENT,对应最大化测量(AT_MOST), 需要子View自己去控制实际的宽高, 但是不能超过父容器的宽高.
 *  但是在可以滑动的列表中, 对应RecyclerView, ScrollView这些, WRAP_CONTENT会变成 UNSPECIFIED.
 *      因为在这些列表的对应方向上,可以允许更大的值, 因为可以滑动 --> 可以超过父容器.
 *
 *  // 除了MATCH_PARENT，固定dp值也是EXACTLY
 * android:layout_width="100dp"  → EXACTLY, 100dp
 * android:layout_width="match_parent" → EXACTLY, 父容器宽度
 *
 *
 * // 2. 为什么有两次测量呢??
 *
 *                                            ========== onMeasure 第1次调用 ==========
 * 19:30:41.421 ALog                       D  [CustomDemoView](CustomDemoView.java:60)#onMeasure-[M][Thread:main-2]
 *                                            widthMeasureSpec - mode: EXACTLY, size: 896
 * 19:30:41.421 ALog                       D  [CustomDemoView](CustomDemoView.java:61)#onMeasure-[M][Thread:main-2]
 *                                            heightMeasureSpec - mode: UNSPECIFIED, size: 1436
 * 19:30:41.422 ALog                       D  [CustomDemoView](CustomDemoView.java:68)#onMeasure-[M][Thread:main-2]
 *                                            期望宽度: 426, 期望高度: 203
 * 19:30:41.423 ALog                       D  [CustomDemoView](CustomDemoView.java:74)#onMeasure-[M][Thread:main-2]
 *                                            最终测量尺寸 - 宽度: 896, 高度: 203
 * 19:30:41.424 ALog                       D  [CustomDemoView](CustomDemoView.java:78)#onMeasure-[M][Thread:main-2]
 *                                            ========== onMeasure 结束 ==========
 * 19:30:41.433 ALog                       D  [CustomDemoView](CustomDemoView.java:52)#onMeasure-[M][Thread:main-2]
 *                                            ========== onMeasure 第2次调用 ==========
 * 19:30:41.433 ALog                       D  [CustomDemoView](CustomDemoView.java:60)#onMeasure-[M][Thread:main-2]
 *                                            widthMeasureSpec - mode: EXACTLY, size: 896
 * 19:30:41.434 ALog                       D  [CustomDemoView](CustomDemoView.java:61)#onMeasure-[M][Thread:main-2]
 *                                            heightMeasureSpec - mode: UNSPECIFIED, size: 1436
 * 19:30:41.434 ALog                       D  [CustomDemoView](CustomDemoView.java:68)#onMeasure-[M][Thread:main-2]
 *                                            期望宽度: 426, 期望高度: 203
 * 19:30:41.435 ALog                       D  [CustomDemoView](CustomDemoView.java:74)#onMeasure-[M][Thread:main-2]
 *                                            最终测量尺寸 - 宽度: 896, 高度: 203
 * 19:30:41.436 ALog                       D  [CustomDemoView](CustomDemoView.java:78)#onMeasure-[M][Thread:main-2]
 *                                            ========== onMeasure 结束 ==========
 *  两次测量, 并且两次测量的结果竟然是一样的??
 *  最可能是LinearLayout：
     * LinearLayout在处理子View时，可能需要多轮测量
     * 特别是当子View有复杂的尺寸要求时
     * 即使结果相同，也会执行完整的测量流程
 *
 *
 *  性能优化, 避免多余的测量.
 *  protected void onMeasure(int widthSpec, int heightSpec) {
 *     // 可以缓存计算结果，避免重复计算
 *     if (lastWidthSpec == widthSpec && lastHeightSpec == heightSpec) {
 *         setMeasuredDimension(lastWidth, lastHeight);
 *         return;
 *     }
 *     // 正常测量逻辑...
 * }
 *
 *
 * // 3. onLayout
 *
 *                                            ========== onLayout 第1次调用 ==========
 * 19:51:21.944 ALog                       D  [CustomDemoView](CustomDemoView.java:86)#onLayout-[M][Thread:main-2]
 *                                            changed: true
 * 19:51:21.944 ALog                       D  [CustomDemoView](CustomDemoView.java:87)#onLayout-[M][Thread:main-2]
 *                                            布局位置 - left: 56, top: 469, right: 952, bottom: 672
 * 19:51:21.944 ALog                       D  [CustomDemoView](CustomDemoView.java:88)#onLayout-[M][Thread:main-2]
 *                                            View尺寸 - 宽度: 896, 高度: 203
 * 19:51:21.945 ALog                       D  [CustomDemoView](CustomDemoView.java:89)#onLayout-[M][Thread:main-2]
 *                                            ========== onLayout 结束 ==========
 *
 *  父容器(LinearLayout)设置的！
 * LinearLayout在自己的onLayout中计算每个子View的位置
 * 调用child.layout(left, top, right, bottom)
 * 这会触发子View的onLayout方法
 *
 * 对于普通View（如CustomDemoView）：
 * onLayout主要用于记录位置信息
 * 可以在这里做一些依赖位置的初始化工作
 *
 * 对于ViewGroup：
 * onLayout中需要布局所有子View
 * 调用每个子View的layout()方法设置位置
 *
 *
 *  对于普通View,没必要去关心onLayout, 对于ViewGroup,才需要去关心.
 *
 *  TODO: 做一个FlexLayout(多个长短不一的标签Layout.)
 *
 *
 *  // 4. onDraw.
 *  19:51:21.947 ALog                       D  [CustomDemoView](CustomDemoView.java:98)#onDraw-[M][Thread:main-2]
 *                                            ========== onDraw 第1次调用 ==========
 * 19:51:21.947 ALog                       D  [CustomDemoView](CustomDemoView.java:99)#onDraw-[M][Thread:main-2]
 *                                            Canvas尺寸 - 宽度: 896, 高度: 203
 * 19:51:21.948 ALog                       D  [CustomDemoView](CustomDemoView.java:131)#onDraw-[M][Thread:main-2]
 *                                            ========== onDraw 结束 ==========
 *
 * 每次需要刷新界面时都会调用
 *  invalidate() → 触发onDraw
 *  滑动、动画、状态变化都会触发
 *
 *  // 5. onTouchEvent.
 * TODO: invalidate , requestLayout 底层原理.
 *
 用户触摸屏幕
 ↓
 1. Activity.dispatchTouchEvent()           // 🎯 起点：Activity接收事件
 ↓
 2. DecorView.dispatchTouchEvent()          // 根ViewGroup
 ↓
 3. Fragment容器.dispatchTouchEvent()        // Fragment的容器
 ↓
 4. ScrollView.dispatchTouchEvent()         // 你的布局文件根View
 ↓
 5. LinearLayout.dispatchTouchEvent()       // binding.main
 ↓
 6. CustomDemoView.dispatchTouchEvent()     // 你的自定义View
 ↓
 7. CustomDemoView.onTouchEvent()           // 🎯 终点：你的触摸处理方法

 向下分发阶段（寻找处理者）
 Activity → DecorView → Fragment → ScrollView → LinearLayout → CustomDemoView

 在这个阶段：
 不需要返回值！事件会自动向下传递
 除非某个ViewGroup主动拦截（onInterceptTouchEvent返回true）
 否则会一直传递到最底层的View

 向上返回阶段（报告处理结果）

 CustomDemoView → LinearLayout → ScrollView → Fragment → DecorView → Activity
 在这个阶段：
 返回值才起作用！
 true = "我处理了这个事件"
 false = "我不处理，请上级处理"

   对于ViewGroup来说;

 TODO : viewGroup的 事件拦截机制. 看看业界重写ViewGroup是怎么处理这个的.
 TODO : 滑动嵌套的时候,是怎么处理触摸事件的.

 // 向下分发阶段（无条件进行）：
 Activity.dispatchTouchEvent()        // 调用DecorView
 └─ DecorView.dispatchTouchEvent()   // 调用Fragment容器
 └─ Fragment.dispatchTouchEvent()  // 调用ScrollView
 └─ ScrollView.dispatchTouchEvent()    // 调用LinearLayout
 └─ LinearLayout.dispatchTouchEvent() // 调用CustomDemoView
 └─ CustomDemoView.dispatchTouchEvent() // 调用onTouchEvent

 // 向上返回阶段（返回值起作用）：
 CustomDemoView.onTouchEvent() → return true
 CustomDemoView.dispatchTouchEvent() → return true
 LinearLayout.dispatchTouchEvent() → return true
 ScrollView.dispatchTouchEvent() → return true
 ...一直向上返回true


 普通View：     没有子View → 不需要拦截 → 没有onInterceptTouchEvent()
 ViewGroup：   有子View   → 需要拦截 → 有onInterceptTouchEvent()


 屏幕上的点击的某块区域,他是怎么精准的计算出,到底要分给什么View的呢??


 */