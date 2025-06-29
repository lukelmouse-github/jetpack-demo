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
 * 中间层的ViewGroup，用于演示事件分发和拦截
 */
public class BViewGroup extends ViewGroup {
    private static final String TAG = "TouchEvent-BViewGroup";

    private Paint mPaint;
    private String mViewGroupName;
    private boolean mInterceptTouchEvent = false;

    public BViewGroup(Context context, String viewGroupName) {
        super(context);
        mViewGroupName = viewGroupName;
        initPaint();
        setWillNotDraw(false); // 允许ViewGroup绘制
        ALog.dd(TAG, mViewGroupName + " 构造方法被调用");
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(20);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ALog.dd(TAG, mViewGroupName + " onMeasure - 开始测量");

        // 测量所有子View
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(
                MeasureSpec.makeMeasureSpec(120, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(80, MeasureSpec.EXACTLY)
            );
        }

        // 设置自身大小
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        ALog.dd(TAG, mViewGroupName + " 测量完成: 宽=" + width + ", 高=" + height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ALog.dd(TAG, mViewGroupName + " onLayout - 开始布局");

        // 横向排列子View
        int childLeft = 20;
        int childTop = 40;

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(childLeft, childTop, childLeft + 120, childTop + 80);
            childLeft += 140; // 每个子View间隔20px
        }

        ALog.dd(TAG, mViewGroupName + " 布局完成");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制背景
        canvas.drawColor(Color.parseColor("#E6E6FA")); // 淡紫色背景

        // 绘制边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.parseColor("#9370DB"));
        canvas.drawRect(2, 2, getWidth() - 2, getHeight() - 2, mPaint);

        // 绘制标题
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        canvas.drawText(mViewGroupName, 10, 25, mPaint);

        // 显示拦截状态
        mPaint.setTextSize(16);
        String interceptText = "拦截: " + (mInterceptTouchEvent ? "开启" : "关闭");
        canvas.drawText(interceptText, 10, getHeight() - 10, mPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ALog.dd(TAG, "========== " + mViewGroupName + " dispatchTouchEvent ==========");
        ALog.dd(TAG, mViewGroupName + " 事件类型: " + getActionString(ev.getAction()));
        ALog.dd(TAG, mViewGroupName + " 坐标: x=" + ev.getX() + ", y=" + ev.getY());

        // 详细记录拦截和分发逻辑
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            logInterceptAndDispatchLogic(ev);
            ALog.dd(TAG, ""); // 空行分隔
            logRealSourceCodeLogic(ev);
        }

        boolean result = super.dispatchTouchEvent(ev);

        ALog.dd(TAG, mViewGroupName + " dispatchTouchEvent 返回: " + result);
        if (result) {
            ALog.dd(TAG, "🔄 " + mViewGroupName + " 向父ViewGroup汇报：事件已处理(return true)");
        } else {
            ALog.dd(TAG, "🔄 " + mViewGroupName + " 向父ViewGroup汇报：事件未处理(return false)");
        }
        ALog.dd(TAG, "========== " + mViewGroupName + " dispatchTouchEvent 结束 ==========");

        return result;
    }



    /**
     * 记录拦截逻辑和分发过程，展示正确的执行顺序
     */
    private void logInterceptAndDispatchLogic(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        ALog.dd(TAG, "🔍 " + mViewGroupName + " 开始事件分发逻辑:");
        ALog.dd(TAG, "🔍 触摸点坐标: (" + x + ", " + y + ")");

        // 第一步：预测拦截逻辑
        ALog.dd(TAG, "🛡️ 第1步：检查是否要拦截事件");
        ALog.dd(TAG, "🛡️ " + mViewGroupName + "当前拦截设置: " + mInterceptTouchEvent);

        if (mInterceptTouchEvent) {
            ALog.dd(TAG, "🛡️ ✅ " + mViewGroupName + "将拦截事件！");
            ALog.dd(TAG, "🛡️ 📋 拦截流程：onInterceptTouchEvent() → true → onTouchEvent()");
            ALog.dd(TAG, "🛡️ 🚫 子View将不会收到此事件");
            return; // 如果拦截，就不需要记录子View匹配了
        }

        // 第二步：子View匹配
        ALog.dd(TAG, "🛡️ ❌ " + mViewGroupName + "不拦截，继续子View匹配");
        ALog.dd(TAG, "🔍 第2步：寻找匹配的子View");
        ALog.dd(TAG, "🔍 " + mViewGroupName + " 尺寸: 宽=" + getWidth() + ", 高=" + getHeight());
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

            ALog.dd(TAG, "🔍 检查子View[" + i + "]: " + getChildViewName(child));
            ALog.dd(TAG, "🔍   边界: left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom);

            // 检查触摸点是否在子View范围内
            if (x >= left && x < right && y >= top && y < bottom) {
                ALog.dd(TAG, "✅ 触摸点在子View[" + i + "]范围内!");
                ALog.dd(TAG, "🎯 匹配成功，将分发给: " + getChildViewName(child));
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
            ALog.dd(TAG, "🎯 第3步：没有匹配的子View，" + mViewGroupName + "自己处理");
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
        if (child instanceof CView) {
            return ((CView) child).getViewName();
        }
        return child.getClass().getSimpleName();
    }

    /**
     * 模拟Android源码中的真实逻辑
     * 对应源码中的关键部分
     */
    private void logRealSourceCodeLogic(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        ALog.dd(TAG, "📖 模拟Android源码逻辑:");
        ALog.dd(TAG, "📖 final int childrenCount = " + getChildCount());
        ALog.dd(TAG, "📖 for (int i = childrenCount - 1; i >= 0; i--) {");

        // 模拟源码中的子View遍历逻辑
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);

            ALog.dd(TAG, "📖   final View child = getChild(" + i + "); // " + getChildViewName(child));

            // 模拟 canReceivePointerEvents 检查
            boolean canReceive = child.getVisibility() == View.VISIBLE && child.isEnabled();
            ALog.dd(TAG, "📖   child.canReceivePointerEvents() = " + canReceive);

            if (!canReceive) {
                ALog.dd(TAG, "📖   continue; // 子View不可接收事件");
                continue;
            }

            // 模拟 isTransformedTouchPointInView 检查
            boolean pointInView = isTransformedTouchPointInView(x, y, child);
            ALog.dd(TAG, "📖   isTransformedTouchPointInView(" + x + ", " + y + ", child) = " + pointInView);

            if (!pointInView) {
                ALog.dd(TAG, "📖   continue; // 触摸点不在子View范围内");
                continue;
            }

            // 找到目标子View
            ALog.dd(TAG, "📖   // 找到目标子View: " + getChildViewName(child));
            ALog.dd(TAG, "📖   if (dispatchTransformedTouchEvent(ev, false, child, idBits)) {");
            ALog.dd(TAG, "📖     // 子View处理了事件，添加到TouchTarget链表");
            ALog.dd(TAG, "📖     newTouchTarget = addTouchTarget(child, idBits);");
            ALog.dd(TAG, "📖     break;");
            ALog.dd(TAG, "📖   }");
            break;
        }

        ALog.dd(TAG, "📖 } // 结束for循环");
    }

    /**
     * 模拟Android源码中的isTransformedTouchPointInView方法
     */
    private boolean isTransformedTouchPointInView(float x, float y, View child) {
        // 简化版的坐标变换和范围检查

        // 1. 坐标变换 (这里简化，实际源码会处理各种变换)
        float localX = x - child.getLeft();
        float localY = y - child.getTop();

        ALog.dd(TAG, "📖     transformPointToViewLocal: (" + x + "," + y + ") -> (" + localX + "," + localY + ")");

        // 2. 检查是否在View范围内 (对应源码中的pointInView方法)
        boolean pointInView = localX >= 0 && localY >= 0 &&
                             localX < child.getWidth() && localY < child.getHeight();

        ALog.dd(TAG, "📖     pointInView(" + localX + ", " + localY + ") = " + pointInView);
        ALog.dd(TAG, "📖     child尺寸: 宽=" + child.getWidth() + ", 高=" + child.getHeight());

        return pointInView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ALog.dd(TAG, "========== " + mViewGroupName + " onInterceptTouchEvent ==========");
        ALog.dd(TAG, mViewGroupName + " 事件类型: " + getActionString(ev.getAction()));
        ALog.dd(TAG, mViewGroupName + " 当前拦截设置: " + mInterceptTouchEvent);

        boolean intercept = mInterceptTouchEvent;

        ALog.dd(TAG, mViewGroupName + " onInterceptTouchEvent 返回: " + intercept);
        ALog.dd(TAG, "========== " + mViewGroupName + " onInterceptTouchEvent 结束 ==========");

        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ALog.dd(TAG, "========== " + mViewGroupName + " onTouchEvent ==========");
        ALog.dd(TAG, mViewGroupName + " 事件类型: " + getActionString(event.getAction()));
        ALog.dd(TAG, mViewGroupName + " 坐标: x=" + event.getX() + ", y=" + event.getY());

        // 如果是拦截状态，处理事件
        if (mInterceptTouchEvent) {
            ALog.dd(TAG, mViewGroupName + " 处理拦截的事件");
            return true;
        }

        ALog.dd(TAG, mViewGroupName + " onTouchEvent 返回: false");
        ALog.dd(TAG, "🤷 " + mViewGroupName + " 表示：我也不知道怎么处理这个事件");
        ALog.dd(TAG, "========== " + mViewGroupName + " onTouchEvent 结束 ==========");

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
        ALog.dd(TAG, mViewGroupName + " 拦截状态设置为: " + intercept);
    }

    public boolean isInterceptTouchEvent() {
        return mInterceptTouchEvent;
    }

    public String getViewGroupName() {
        return mViewGroupName;
    }
}