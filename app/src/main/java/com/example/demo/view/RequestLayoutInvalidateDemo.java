package com.example.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.common.log.ALog;

/**
 * requestLayout和invalidate底层原理演示
 * 详细展示两种更新机制的区别和应用场景
 */
public class RequestLayoutInvalidateDemo extends View {
    private static final String TAG = "RequestLayoutInvalidate";
    
    private Paint mPaint;
    private int mCurrentWidth = 300;
    private int mCurrentHeight = 200;
    private int mCurrentColor = Color.parseColor("#2196F3");
    private String mCurrentText = "点击测试";
    
    private int mMeasureCount = 0;
    private int mLayoutCount = 0;
    private int mDrawCount = 0;

    public RequestLayoutInvalidateDemo(Context context) {
        super(context);
        init();
    }

    public RequestLayoutInvalidateDemo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(20);
        mPaint.setTextAlign(Paint.Align.CENTER);
        ALog.dd(TAG, "RequestLayoutInvalidateDemo 初始化完成");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureCount++;
        ALog.dd(TAG, "🔄 onMeasure被调用 - 第" + mMeasureCount + "次");
        
        // 记录测量规格
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        ALog.dd(TAG, "父View约束 - 宽:" + getModeString(widthMode) + " " + widthSize);
        ALog.dd(TAG, "父View约束 - 高:" + getModeString(heightMode) + " " + heightSize);
        
        // 使用当前设定的尺寸
        int finalWidth, finalHeight;
        
        if (widthMode == MeasureSpec.EXACTLY) {
            finalWidth = widthSize;
        } else {
            finalWidth = Math.min(mCurrentWidth, widthSize);
        }
        
        if (heightMode == MeasureSpec.EXACTLY) {
            finalHeight = heightSize;
        } else {
            finalHeight = Math.min(mCurrentHeight, heightSize);
        }
        
        ALog.dd(TAG, "测量结果: " + finalWidth + " x " + finalHeight);
        setMeasuredDimension(finalWidth, finalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mLayoutCount++;
        ALog.dd(TAG, "📐 onLayout被调用 - 第" + mLayoutCount + "次");
        ALog.dd(TAG, "changed: " + changed);
        ALog.dd(TAG, "位置: (" + left + ", " + top + ", " + right + ", " + bottom + ")");
        ALog.dd(TAG, "实际尺寸: " + getWidth() + " x " + getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mDrawCount++;
        ALog.dd(TAG, "🎨 onDraw被调用 - 第" + mDrawCount + "次");
        
        // 绘制背景
        canvas.drawColor(mCurrentColor);
        
        // 绘制边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(4, 4, getWidth() - 4, getHeight() - 4, mPaint);
        
        // 绘制文字
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(24);
        mPaint.setFakeBoldText(true);
        
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        
        canvas.drawText(mCurrentText, centerX, centerY - 30, mPaint);
        
        // 显示统计信息
        mPaint.setTextSize(16);
        mPaint.setFakeBoldText(false);
        canvas.drawText("尺寸: " + getWidth() + "x" + getHeight(), centerX, centerY, mPaint);
        canvas.drawText("测量: " + mMeasureCount + " 布局: " + mLayoutCount + " 绘制: " + mDrawCount, 
                       centerX, centerY + 25, mPaint);
    }

    /**
     * 演示requestLayout()的使用场景
     * 改变View的尺寸，需要重新测量和布局
     */
    public void testRequestLayout() {
        ALog.dd(TAG, "🔄 ========== 测试requestLayout() ==========");
        ALog.dd(TAG, "改变View尺寸，触发完整的测量→布局→绘制流程");
        
        // 改变尺寸
        int oldWidth = mCurrentWidth;
        int oldHeight = mCurrentHeight;
        mCurrentWidth = (int) (200 + Math.random() * 400);
        mCurrentHeight = (int) (150 + Math.random() * 300);
        mCurrentText = "requestLayout";
        
        ALog.dd(TAG, "尺寸变化: " + oldWidth + "x" + oldHeight + " → " + mCurrentWidth + "x" + mCurrentHeight);
        ALog.dd(TAG, "调用requestLayout()...");
        
        // 🔥 核心调用
        requestLayout();
        
        ALog.dd(TAG, "requestLayout()调用完成，等待系统调度...");
    }

    /**
     * 演示invalidate()的使用场景
     * 只改变外观，无需重新测量和布局
     */
    public void testInvalidate() {
        ALog.dd(TAG, "🎨 ========== 测试invalidate() ==========");
        ALog.dd(TAG, "只改变View外观，只触发绘制流程");
        
        // 改变颜色
        int oldColor = mCurrentColor;
        int[] colors = {
            Color.parseColor("#F44336"), // 红色
            Color.parseColor("#4CAF50"), // 绿色  
            Color.parseColor("#2196F3"), // 蓝色
            Color.parseColor("#FF9800"), // 橙色
            Color.parseColor("#9C27B0")  // 紫色
        };
        mCurrentColor = colors[(int) (Math.random() * colors.length)];
        mCurrentText = "invalidate";
        
        ALog.dd(TAG, "颜色变化: " + Integer.toHexString(oldColor) + " → " + Integer.toHexString(mCurrentColor));
        ALog.dd(TAG, "调用invalidate()...");
        
        // 🔥 核心调用
        invalidate();
        
        ALog.dd(TAG, "invalidate()调用完成，等待系统调度...");
    }

    /**
     * 重置计数器
     */
    public void resetCounters() {
        mMeasureCount = 0;
        mLayoutCount = 0;
        mDrawCount = 0;
        ALog.dd(TAG, "🔄 计数器已重置");
        invalidate(); // 重新绘制以更新显示的计数
    }

    /**
     * 获取当前统计信息
     */
    public String getStats() {
        return "测量:" + mMeasureCount + " 布局:" + mLayoutCount + " 绘制:" + mDrawCount;
    }

    private String getModeString(int mode) {
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return "EXACTLY";
            case MeasureSpec.AT_MOST:
                return "AT_MOST";
            case MeasureSpec.UNSPECIFIED:
                return "UNSPECIFIED";
            default:
                return "UNKNOWN";
        }
    }
}

/*
===============================================================
🎯 requestLayout() vs invalidate() 底层原理详解
===============================================================

1. requestLayout()底层流程：
┌─────────────────────────────────────────┐
│ View.requestLayout()                    │
│ ├─ 设置PFLAG_FORCE_LAYOUT标志           │
│ ├─ 设置PFLAG_INVALIDATED标志            │
│ ├─ 向父View递归调用requestLayout()       │
│ └─ 直到ViewRootImpl.requestLayout()     │
│                                         │
│ ViewRootImpl.requestLayout()            │
│ ├─ scheduleTraversals()                 │
│ └─ 通过Choreographer调度下一帧           │
│                                         │
│ performTraversals()                     │
│ ├─ performMeasure() ← 🔄 重新测量       │
│ ├─ performLayout()  ← 📐 重新布局       │
│ └─ performDraw()    ← 🎨 重新绘制       │
└─────────────────────────────────────────┘

2. invalidate()底层流程：
┌─────────────────────────────────────────┐
│ View.invalidate()                       │
│ ├─ 设置PFLAG_DIRTY标志                  │
│ ├─ 向父View递归调用invalidateChild()    │
│ └─ 直到ViewRootImpl.invalidateChild()   │
│                                         │
│ ViewRootImpl.invalidateChild()          │
│ ├─ 只标记绘制区域为脏区域               │
│ └─ scheduleTraversals()                 │
│                                         │
│ performTraversals()                     │
│ ├─ performMeasure() ← ❌ 跳过测量       │
│ ├─ performLayout()  ← ❌ 跳过布局       │
│ └─ performDraw()    ← 🎨 只执行绘制     │
└─────────────────────────────────────────┘

3. 关键标志位：
• PFLAG_FORCE_LAYOUT: 强制重新测量布局
• PFLAG_INVALIDATED: 标记为已失效
• PFLAG_DIRTY: 标记为需要重绘
• PFLAG_DRAWN: 标记为已绘制

4. 性能对比：
requestLayout(): 完整流程，开销大
invalidate(): 只绘制，开销小

5. 使用场景：
requestLayout():
• 改变View尺寸
• 改变View位置
• 添加/删除子View
• 改变布局参数

invalidate():
• 改变颜色
• 改变文字内容
• 改变透明度
• 改变背景图片

*/