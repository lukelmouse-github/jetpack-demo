package com.example.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.common.log.ALog;

/**
 * 最外层的ViewGroup，包含B1和B2两个ViewGroup
 */
public class AViewGroup extends ViewGroup {
    private static final String TAG = "TouchEvent-AViewGroup";

    private Paint mPaint;
    private boolean mInterceptTouchEvent = false;
    private BViewGroup mB1ViewGroup;
    private BViewGroup mB2ViewGroup;

    public AViewGroup(Context context) {
        super(context);
        initPaint();
        setWillNotDraw(false); // 允许ViewGroup绘制
        ALog.dd(TAG, "AViewGroup 构造方法被调用");
        initChildViews();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(24);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void initChildViews() {
        // 创建B1-ViewGroup
        mB1ViewGroup = new BViewGroup(getContext(), "B1-ViewGroup");
        // 为B1添加C11和C12子View
        mB1ViewGroup.addView(new CView(getContext(), "C11-View"));
        mB1ViewGroup.addView(new CView(getContext(), "C12-View"));
        addView(mB1ViewGroup);

        // 创建B2-ViewGroup
        mB2ViewGroup = new BViewGroup(getContext(), "B2-ViewGroup");
        // 为B2添加C21和C22子View
        mB2ViewGroup.addView(new CView(getContext(), "C21-View"));
        mB2ViewGroup.addView(new CView(getContext(), "C22-View"));
        addView(mB2ViewGroup);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ALog.dd(TAG, "AViewGroup onMeasure - 开始测量");

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // 为每个B ViewGroup分配空间
        int bViewGroupWidth = width - 40; // 左右各留20px边距
        int bViewGroupHeight = (height - 80) / 2; // 上下各留20px，中间分两部分

        // 测量B1 ViewGroup
        mB1ViewGroup.measure(
            MeasureSpec.makeMeasureSpec(bViewGroupWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(bViewGroupHeight, MeasureSpec.EXACTLY)
        );

        // 测量B2 ViewGroup
        mB2ViewGroup.measure(
            MeasureSpec.makeMeasureSpec(bViewGroupWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(bViewGroupHeight, MeasureSpec.EXACTLY)
        );

        setMeasuredDimension(width, height);
        ALog.dd(TAG, "AViewGroup 测量完成: 宽=" + width + ", 高=" + height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ALog.dd(TAG, "AViewGroup onLayout - 开始布局");

        int bViewGroupWidth = getWidth() - 40;
        int bViewGroupHeight = (getHeight() - 80) / 2;

        // 布局B1-ViewGroup (上半部分)
        mB1ViewGroup.layout(20, 40, 20 + bViewGroupWidth, 40 + bViewGroupHeight);

        // 布局B2-ViewGroup (下半部分)
        int b2Top = 40 + bViewGroupHeight + 20; // B1底部 + 间距
        mB2ViewGroup.layout(20, b2Top, 20 + bViewGroupWidth, b2Top + bViewGroupHeight);

        ALog.dd(TAG, "AViewGroup 布局完成");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制背景
        canvas.drawColor(Color.parseColor("#F0F8FF")); // 浅蓝色背景

        // 绘制边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.parseColor("#4169E1"));
        canvas.drawRect(3, 3, getWidth() - 3, getHeight() - 3, mPaint);

        // 绘制标题
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(24);
        canvas.drawText("A-ViewGroup", 10, 30, mPaint);

        // 显示拦截状态
        mPaint.setTextSize(18);
        String interceptText = "拦截: " + (mInterceptTouchEvent ? "开启" : "关闭");
        canvas.drawText(interceptText, getWidth() - 120, 30, mPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ALog.dd(TAG, "========== AViewGroup dispatchTouchEvent ==========");
        ALog.dd(TAG, "AViewGroup 事件类型: " + getActionString(ev.getAction()));
        ALog.dd(TAG, "AViewGroup 坐标: x=" + ev.getX() + ", y=" + ev.getY());

        // 详细记录拦截逻辑判断过程
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            logInterceptAndDispatchLogic(ev);
        }

        boolean result = super.dispatchTouchEvent(ev);

        ALog.dd(TAG, "AViewGroup dispatchTouchEvent 返回: " + result);
        if (result) {
            ALog.dd(TAG, "🔄 AViewGroup 向Activity汇报：事件已处理(return true)");
        } else {
            ALog.dd(TAG, "🔄 AViewGroup 向Activity汇报：事件未处理(return false)");
        }
        ALog.dd(TAG, "========== AViewGroup dispatchTouchEvent 结束 ==========");

        return result;
    }

    /**
     * 记录拦截逻辑和分发过程，展示正确的执行顺序
     */
    private void logInterceptAndDispatchLogic(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        ALog.dd(TAG, "🔍 AViewGroup 开始事件分发逻辑:");
        ALog.dd(TAG, "🔍 触摸点坐标: (" + x + ", " + y + ")");

        // 第一步：预测拦截逻辑
        ALog.dd(TAG, "🛡️ 第1步：检查是否要拦截事件");
        ALog.dd(TAG, "🛡️ AViewGroup当前拦截设置: " + mInterceptTouchEvent);

        if (mInterceptTouchEvent) {
            ALog.dd(TAG, "🛡️ ✅ AViewGroup将拦截事件！");
            ALog.dd(TAG, "🛡️ 📋 拦截流程：onInterceptTouchEvent() → true → onTouchEvent()");
            ALog.dd(TAG, "🛡️ 🚫 子View将不会收到此事件");
            return; // 如果拦截，就不需要记录子View匹配了
        }

        // 第二步：子View匹配
        ALog.dd(TAG, "🛡️ ❌ AViewGroup不拦截，继续子View匹配");
        ALog.dd(TAG, "🔍 第2步：寻找匹配的子View");
        ALog.dd(TAG, "🔍 AViewGroup 尺寸: 宽=" + getWidth() + ", 高=" + getHeight());
        ALog.dd(TAG, "🔍 子View数量: " + getChildCount());

        View matchedChild = null;

        // 从后往前遍历子View (后添加的在上层，优先处理)
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);

            // 获取子View的边界
            int left = child.getLeft();
            int top = child.getTop();
            int right = child.getRight();
            int bottom = child.getBottom();

            String childName = getChildViewName(child);
            ALog.dd(TAG, "🔍 检查子View[" + i + "]: " + childName);
            ALog.dd(TAG, "🔍   边界: left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom);

            // 检查触摸点是否在子View范围内
            if (x >= left && x < right && y >= top && y < bottom) {
                ALog.dd(TAG, "✅ 触摸点在子View[" + i + "]范围内!");
                ALog.dd(TAG, "🎯 匹配成功，将分发给: " + childName);
                matchedChild = child;
                break;
            } else {
                ALog.dd(TAG, "❌ 触摸点不在子View[" + i + "]范围内");
                // 详细分析为什么不匹配
                if (x < left) ALog.dd(TAG, "   原因: x坐标(" + x + ") < left边界(" + left + ")");
                else if (x >= right) ALog.dd(TAG, "   原因: x坐标(" + x + ") >= right边界(" + right + ")");
                if (y < top) ALog.dd(TAG, "   原因: y坐标(" + y + ") < top边界(" + top + ")");
                else if (y >= bottom) ALog.dd(TAG, "   原因: y坐标(" + y + ") >= bottom边界(" + bottom + ")");
            }
        }

        if (matchedChild == null) {
            ALog.dd(TAG, "🎯 第3步：没有匹配的子View，AViewGroup自己处理");
            ALog.dd(TAG, "🎯 流程：dispatchTouchEvent() → onTouchEvent()");
        } else {
            ALog.dd(TAG, "🎯 第3步：分发给匹配的子View");
            ALog.dd(TAG, "🎯 流程：dispatchTouchEvent() → " + getChildViewName(matchedChild) + ".dispatchTouchEvent()");
        }
    }

    /**
     * 获取子View的名称用于日志输出
     */
    private String getChildViewName(View child) {
        if (child instanceof BViewGroup) {
            return ((BViewGroup) child).getViewGroupName();
        }
        return child.getClass().getSimpleName();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ALog.dd(TAG, "========== AViewGroup onInterceptTouchEvent ==========");
        ALog.dd(TAG, "AViewGroup 事件类型: " + getActionString(ev.getAction()));
        ALog.dd(TAG, "AViewGroup 当前拦截设置: " + mInterceptTouchEvent);

        boolean intercept = mInterceptTouchEvent;

        ALog.dd(TAG, "AViewGroup onInterceptTouchEvent 返回: " + intercept);
        ALog.dd(TAG, "========== AViewGroup onInterceptTouchEvent 结束 ==========");

        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ALog.dd(TAG, "========== AViewGroup onTouchEvent ==========");
        ALog.dd(TAG, "AViewGroup 事件类型: " + getActionString(event.getAction()));
        ALog.dd(TAG, "AViewGroup 坐标: x=" + event.getX() + ", y=" + event.getY());

        // 如果是拦截状态，处理事件
        if (mInterceptTouchEvent) {
            ALog.dd(TAG, "AViewGroup 处理拦截的事件");
            return true;
        }

        ALog.dd(TAG, "AViewGroup onTouchEvent 返回: false");
        ALog.dd(TAG, "========== AViewGroup onTouchEvent 结束 ==========");

        return false;
    }

    private String getActionString(int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            case MotionEvent.ACTION_CANCEL:
                return "ACTION_CANCEL";
            default:
                return "ACTION_" + action;
        }
    }

    public void setInterceptTouchEvent(boolean intercept) {
        this.mInterceptTouchEvent = intercept;
        invalidate();
        ALog.dd(TAG, "AViewGroup 拦截状态设置为: " + intercept);
    }

    public boolean isInterceptTouchEvent() {
        return mInterceptTouchEvent;
    }

    public BViewGroup getB1ViewGroup() {
        return mB1ViewGroup;
    }

    public BViewGroup getB2ViewGroup() {
        return mB2ViewGroup;
    }
}