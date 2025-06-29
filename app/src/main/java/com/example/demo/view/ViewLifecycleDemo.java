package com.example.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.common.log.ALog;

/**
 * 演示View生命周期回调的自定义View
 */
public class ViewLifecycleDemo extends View {
    private static final String TAG = "ViewLifecycleDemo";
    
    private Paint mPaint;

    public ViewLifecycleDemo(Context context) {
        this(context, null);
    }

    public ViewLifecycleDemo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewLifecycleDemo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ALog.dd(TAG, "构造方法被调用");
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(24);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ALog.dd(TAG, "onAttachedToWindow - View被附加到窗口");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ALog.dd(TAG, "onDetachedFromWindow - View从窗口分离");
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        String visibilityStr = getVisibilityString(visibility);
        ALog.dd(TAG, "onVisibilityChanged - 可见性变化: " + visibilityStr);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        String visibilityStr = getVisibilityString(visibility);
        ALog.dd(TAG, "onWindowVisibilityChanged - 窗口可见性变化: " + visibilityStr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ALog.dd(TAG, "onSizeChanged - 尺寸变化");
        ALog.dd(TAG, "新尺寸: 宽=" + w + ", 高=" + h);
        ALog.dd(TAG, "旧尺寸: 宽=" + oldw + ", 高=" + oldh);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ALog.dd(TAG, "onFinishInflate - 从XML加载完成");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ALog.dd(TAG, "onMeasure 被调用");
        
        // 设置一个固定大小
        int width = 300;
        int height = 100;
        
        setMeasuredDimension(width, height);
        ALog.dd(TAG, "设置测量尺寸: 宽=" + width + ", 高=" + height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ALog.dd(TAG, "onLayout - 布局确定");
        ALog.dd(TAG, "位置: left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ALog.dd(TAG, "onDraw - 开始绘制");
        
        // 绘制背景
        canvas.drawColor(Color.CYAN);
        
        // 绘制文本
        mPaint.setColor(Color.BLACK);
        canvas.drawText("View生命周期Demo", 10, 30, mPaint);
        canvas.drawText("查看日志了解回调流程", 10, 60, mPaint);
        
        ALog.dd(TAG, "onDraw - 绘制完成");
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, android.graphics.Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        ALog.dd(TAG, "onFocusChanged - 焦点变化: " + (gainFocus ? "获得焦点" : "失去焦点"));
    }

    private String getVisibilityString(int visibility) {
        switch (visibility) {
            case View.VISIBLE:
                return "VISIBLE";
            case View.INVISIBLE:
                return "INVISIBLE";
            case View.GONE:
                return "GONE";
            default:
                return "UNKNOWN";
        }
    }
}