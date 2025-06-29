package com.example.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.common.log.ALog;

/**
 * 多层嵌套的触摸事件分发演示
 */
public class NestedTouchEventDemo extends ViewGroup {
    private static final String TAG = "NestedTouchDemo";

    private Paint mPaint;
    private String mName;
    private int mLevel;
    private int mColor;

    public NestedTouchEventDemo(Context context, String name, int level, int color) {
        super(context);
        this.mName = name;
        this.mLevel = level;
        this.mColor = color;
        ALog.dd(TAG, mName + " 构造方法被调用，层级：" + mLevel);
        initPaint();
        setWillNotDraw(false);

        // 如果不是最深层，添加子ViewGroup
        if (level < 3) {
            addChildViewGroup();
        }
        
        // 最深层添加一个普通View
        if (level == 3) {
            addChildView();
        }
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(16);
        mPaint.setColor(Color.BLACK);
    }

    private void addChildViewGroup() {
        String childName = mName + "_Child";
        int childColor = mColor + 0x202020; // 颜色逐级变深
        NestedTouchEventDemo child = new NestedTouchEventDemo(
            getContext(), childName, mLevel + 1, childColor);
        
        LayoutParams params = new LayoutParams(
            LayoutParams.MATCH_PARENT, 
            LayoutParams.MATCH_PARENT
        );
        addView(child, params);
    }

    private void addChildView() {
        View childView = new View(getContext()) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                ALog.dd(TAG, "最内层子View onTouchEvent: " + getActionString(event.getAction()));
                ALog.dd(TAG, "最内层子View 坐标: x=" + event.getX() + ", y=" + event.getY());
                // 返回true表示消费事件
                return true;
            }
        };
        childView.setBackgroundColor(Color.CYAN);
        
        LayoutParams params = new LayoutParams(80, 40);
        addView(childView, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ALog.dd(TAG, mName + " onMeasure 开始");

        // 简单的测量逻辑
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        ALog.dd(TAG, mName + " onMeasure 结束: " + width + " x " + height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ALog.dd(TAG, mName + " onLayout");

        // 布局子View，每层缩小一些并偏移
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            
            int margin = mLevel * 20; // 每层缩进20px
            int childLeft = margin;
            int childTop = margin;
            int childRight = getWidth() - margin;
            int childBottom = getHeight() - margin;

            child.layout(childLeft, childTop, childRight, childBottom);
            
            ALog.dd(TAG, mName + " 子View布局: (" + childLeft + ", " + childTop + ", " 
                   + childRight + ", " + childBottom + ")");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制当前ViewGroup的边框和信息
        canvas.drawColor(mColor);
        
        // 绘制边框
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(5, 5, getWidth() - 5, getHeight() - 5, mPaint);

        // 绘制文字信息
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(mName, 10, 25, mPaint);
        canvas.drawText("Level " + mLevel, 10, 45, mPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ALog.dd(TAG, "========== " + mName + " dispatchTouchEvent ==========");
        ALog.dd(TAG, mName + " 事件类型: " + getActionString(ev.getAction()));
        ALog.dd(TAG, mName + " 坐标: x=" + ev.getX() + ", y=" + ev.getY());

        boolean result = super.dispatchTouchEvent(ev);

        ALog.dd(TAG, mName + " dispatchTouchEvent 返回: " + result);
        ALog.dd(TAG, "========== " + mName + " dispatchTouchEvent 结束 ==========");

        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ALog.dd(TAG, "========== " + mName + " onInterceptTouchEvent ==========");
        ALog.dd(TAG, mName + " 事件类型: " + getActionString(ev.getAction()));

        // 第2层（Parent_Child）拦截DOWN事件，演示事件拦截
        boolean intercept = mLevel == 2 && ev.getAction() == MotionEvent.ACTION_DOWN;

        ALog.dd(TAG, mName + " onInterceptTouchEvent 返回: " + intercept);
        ALog.dd(TAG, "========== " + mName + " onInterceptTouchEvent 结束 ==========");

        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ALog.dd(TAG, "========== " + mName + " onTouchEvent ==========");
        ALog.dd(TAG, mName + " 事件类型: " + getActionString(event.getAction()));
        ALog.dd(TAG, mName + " 坐标: x=" + event.getX() + ", y=" + event.getY());

        // 只有第2层处理触摸事件
        boolean handled = mLevel == 2;

        ALog.dd(TAG, mName + " onTouchEvent 返回: " + handled);
        ALog.dd(TAG, "========== " + mName + " onTouchEvent 结束 ==========");

        return handled;
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
    
    /**
     * 创建根ViewGroup用于演示
     */
    public static NestedTouchEventDemo createDemo(Context context) {
        return new NestedTouchEventDemo(context, "Parent", 1, 0xFFE0E0E0);
    }
}