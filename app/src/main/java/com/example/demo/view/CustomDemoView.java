package com.example.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.common.log.ALog;

/**
 * 演示View的测量、布局、绘制流程的自定义View
 */
public class CustomDemoView extends View {
    private static final String TAG = "CustomDemoView";
    
    private Paint mPaint;
    private String mText = "自定义View Demo";
    private int mMeasureCount = 0;
    private int mLayoutCount = 0;
    private int mDrawCount = 0;
    private float mTouchX, mTouchY;
    private boolean mIsTouched = false;

    public CustomDemoView(Context context) {
        this(context, null);
    }

    public CustomDemoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomDemoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ALog.dd(TAG, "构造方法被调用");
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(48);
        mPaint.setColor(Color.BLACK);
        ALog.dd(TAG, "初始化Paint完成");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureCount++;
        
        ALog.dd(TAG, "========== onMeasure 第" + mMeasureCount + "次调用 ==========");
        
        // 解析MeasureSpec
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        ALog.dd(TAG, "widthMeasureSpec - mode: " + getModeString(widthMode) + ", size: " + widthSize);
        ALog.dd(TAG, "heightMeasureSpec - mode: " + getModeString(heightMode) + ", size: " + heightSize);
        
        // 计算期望的宽高
        int desiredWidth = (int) (mPaint.measureText(mText) + getPaddingLeft() + getPaddingRight());
        int desiredHeight = (int) (mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top) 
                           + getPaddingTop() + getPaddingBottom() + 100; // 额外留一些空间显示触摸点
        
        ALog.dd(TAG, "期望宽度: " + desiredWidth + ", 期望高度: " + desiredHeight);
        
        // 根据MeasureSpec确定最终尺寸
        int finalWidth = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0);
        int finalHeight = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0);
        
        ALog.dd(TAG, "最终测量尺寸 - 宽度: " + finalWidth + ", 高度: " + finalHeight);
        
        setMeasuredDimension(finalWidth, finalHeight);
        
        ALog.dd(TAG, "========== onMeasure 结束 ==========");
    }

    /**
     * dispatchTouchEvent() 的工作流程：
     * 1. 接收来自父View的触摸事件
     * 2. 决定如何处理这个事件
     * 3. 对于普通View：直接调用自己的onTouchEvent()
     * 4. 对于ViewGroup：先检查是否拦截，再决定给子View还是自己
     *
     * 4. onInterceptTouchEvent()  // ViewGroup专用，拦截子View事件
     * @param event The motion event to be dispatched.
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        ALog.dd(TAG, "========== dispatchTouchEvent ==========");
        ALog.dd(TAG, "分发事件: " + getActionString(event.getAction()));
        ALog.dd(TAG, "分发坐标: x=" + event.getX() + ", y=" + event.getY());
        ALog.dd(TAG, "分发时间: " + System.currentTimeMillis());

        // 调用父类的分发逻辑，对于普通View会调用onTouchEvent
        boolean result = super.dispatchTouchEvent(event);

        ALog.dd(TAG, "dispatchTouchEvent 返回: " + result);
        ALog.dd(TAG, "========== dispatchTouchEvent 结束 ==========");

        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mLayoutCount++;
        
        ALog.dd(TAG, "========== onLayout 第" + mLayoutCount + "次调用 ==========");
        ALog.dd(TAG, "changed: " + changed);
        ALog.dd(TAG, "布局位置 - left: " + left + ", top: " + top + ", right: " + right + ", bottom: " + bottom);
        ALog.dd(TAG, "View尺寸 - 宽度: " + getWidth() + ", 高度: " + getHeight());
        ALog.dd(TAG, "========== onLayout 结束 ==========");
        
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mDrawCount++;
        
        ALog.dd(TAG, "========== onDraw 第" + mDrawCount + "次调用 ==========");
        ALog.dd(TAG, "Canvas尺寸 - 宽度: " + canvas.getWidth() + ", 高度: " + canvas.getHeight());
        
        // 绘制背景
        canvas.drawColor(mIsTouched ? Color.LTGRAY : Color.WHITE);
        
        // 绘制文本
        float textX = getPaddingLeft();
        float textY = getPaddingTop() - mPaint.getFontMetrics().top;
        canvas.drawText(mText, textX, textY, mPaint);
        
        // 绘制测量、布局、绘制次数信息
        mPaint.setTextSize(24);
        mPaint.setColor(Color.BLUE);
        String info = "测量:" + mMeasureCount + " 布局:" + mLayoutCount + " 绘制:" + mDrawCount;
        canvas.drawText(info, getPaddingLeft(), textY + 50, mPaint);
        
        // 如果有触摸，绘制触摸点
        if (mIsTouched) {
            mPaint.setColor(Color.RED);
            canvas.drawCircle(mTouchX, mTouchY, 20, mPaint);
            
            mPaint.setColor(Color.GREEN);
            mPaint.setTextSize(20);
            String touchInfo = "触摸点: (" + (int)mTouchX + ", " + (int)mTouchY + ")";
            canvas.drawText(touchInfo, getPaddingLeft(), textY + 80, mPaint);
            ALog.dd(TAG, touchInfo);
        }
        
        // 恢复画笔设置
        mPaint.setTextSize(48);
        mPaint.setColor(Color.BLACK);
        
        ALog.dd(TAG, "========== onDraw 结束 ==========");
    }


    /**
     *     // 处理用户的触摸交互
     *     // 返回true=消费事件，返回false=不处理事件
     * @param event The motion event.
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ALog.dd(TAG, "========== onTouchEvent ==========");
        ALog.dd(TAG, "触摸事件: " + getActionString(event.getAction()));
        ALog.dd(TAG, "触摸坐标: x=" + event.getX() + ", y=" + event.getY());
        ALog.dd(TAG, "原始坐标: rawX=" + event.getRawX() + ", rawY=" + event.getRawY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouched = true;
                mTouchX = event.getX();
                mTouchY = event.getY();
                invalidate();
                ALog.dd(TAG, "ACTION_DOWN - 触摸开始");
                break;

            case MotionEvent.ACTION_MOVE:
                mTouchX = event.getX();
                mTouchY = event.getY();
                invalidate();
                ALog.dd(TAG, "ACTION_MOVE - 移动中");
                break;

            case MotionEvent.ACTION_UP:
                mIsTouched = false;
                invalidate();
                ALog.dd(TAG, "ACTION_UP - 触摸结束");
                break;

            case MotionEvent.ACTION_CANCEL:
                mIsTouched = false;
                invalidate();
                ALog.dd(TAG, "ACTION_CANCEL - 触摸取消");
                break;
        }

        ALog.dd(TAG, "onTouchEvent 返回: true");
        return true; // 🔥 修复：始终返回true，确保处理完整触摸序列
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
     * 重置计数器
     */
    public void resetCounters() {
        mMeasureCount = 0;
        mLayoutCount = 0;
        mDrawCount = 0;
        ALog.dd(TAG, "计数器已重置");
    }
}