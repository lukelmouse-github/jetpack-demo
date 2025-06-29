package com.example.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.common.log.ALog;

/**
 * 对比演示onWindowVisibilityChanged和onVisibilityChanged的区别
 */
public class VisibilityComparisonDemo extends View {
    private static final String TAG = "VisibilityComparison";
    
    private Paint mPaint;
    private String mCurrentStatus = "初始状态";

    public VisibilityComparisonDemo(Context context) {
        super(context);
        init();
    }

    public VisibilityComparisonDemo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(40);
        mPaint.setColor(Color.BLACK);
        ALog.d(TAG, "VisibilityComparisonDemo 构造完成");
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        
        String visibilityStr = getVisibilityString(visibility);
        ALog.d(TAG, "🪟 onWindowVisibilityChanged: " + visibilityStr);
        ALog.d(TAG, "🪟 这是窗口级别的可见性变化，通常由应用前后台切换触发");
        
        mCurrentStatus = "窗口: " + visibilityStr;
        invalidate(); // 重新绘制
        
        // 实际应用场景
        if (visibility == View.VISIBLE) {
            ALog.d(TAG, "🪟 窗口可见 - 可以恢复耗资源操作（动画、视频播放等）");
        } else {
            ALog.d(TAG, "🪟 窗口不可见 - 应该暂停耗资源操作，节省电量");
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        
        String visibilityStr = getVisibilityString(visibility);
        String viewName = (changedView == this) ? "自己" : "祖先View";
        
        ALog.d(TAG, "👁️ onVisibilityChanged: " + viewName + " → " + visibilityStr);
        ALog.d(TAG, "👁️ 这是View级别的可见性变化，由setVisibility()调用触发");
        
        if (changedView == this) {
            mCurrentStatus = "View: " + visibilityStr;
            invalidate(); // 重新绘制
            
            // 实际应用场景
            if (visibility == View.VISIBLE) {
                ALog.d(TAG, "👁️ View可见 - 可以开始曝光统计、动画等");
            } else {
                ALog.d(TAG, "👁️ View不可见 - 应该停止动画、清理资源等");
            }
        } else {
            ALog.d(TAG, "👁️ 父View/祖先View的可见性改变影响了当前View");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // 绘制背景
        canvas.drawColor(Color.parseColor("#E8F4FD"));
        
        // 绘制边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.parseColor("#2196F3"));
        canvas.drawRect(4, 4, getWidth() - 4, getHeight() - 4, mPaint);
        
        // 绘制标题
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(32);
        canvas.drawText("可见性回调对比演示", 20, 50, mPaint);
        
        // 绘制当前状态
        mPaint.setTextSize(24);
        mPaint.setColor(Color.parseColor("#FF5722"));
        canvas.drawText("当前状态: " + mCurrentStatus, 20, 100, mPaint);
        
        // 绘制说明
        mPaint.setTextSize(18);
        mPaint.setColor(Color.DKGRAY);
        canvas.drawText("🪟 onWindowVisibilityChanged: 监听窗口可见性", 20, 140, mPaint);
        canvas.drawText("   - 应用前后台切换时触发", 20, 165, mPaint);
        canvas.drawText("   - 用于性能优化和资源管理", 20, 190, mPaint);
        
        canvas.drawText("👁️ onVisibilityChanged: 监听View可见性", 20, 230, mPaint);
        canvas.drawText("   - setVisibility()调用时触发", 20, 255, mPaint);
        canvas.drawText("   - 用于UI状态管理和曝光统计", 20, 280, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置固定高度
        int height = 320;
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            height
        );
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
                return "UNKNOWN(" + visibility + ")";
        }
    }

    // 提供测试方法
    public void testVisibilityChange() {
        ALog.d(TAG, "🧪 开始测试View可见性变化...");
        
        // 这会触发onVisibilityChanged，但不会触发onWindowVisibilityChanged
        post(() -> {
            ALog.d(TAG, "🧪 设置为INVISIBLE");
            setVisibility(View.INVISIBLE);
        });
        
        postDelayed(() -> {
            ALog.d(TAG, "🧪 设置为VISIBLE");  
            setVisibility(View.VISIBLE);
        }, 2000);
        
        postDelayed(() -> {
            ALog.d(TAG, "🧪 设置为GONE");
            setVisibility(View.GONE);
        }, 4000);
        
        postDelayed(() -> {
            ALog.d(TAG, "🧪 恢复为VISIBLE");
            setVisibility(View.VISIBLE);
        }, 6000);
    }
}