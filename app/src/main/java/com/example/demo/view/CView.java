package com.example.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.example.common.log.ALog;

/**
 * 最内层的View，用于演示事件处理
 */
public class CView extends View {
    private static final String TAG = "TouchEvent-CView";

    private Paint mPaint;
    private String mViewName;
    private boolean mIsPressed = false;

    public CView(Context context, String viewName) {
        super(context);
        mViewName = viewName;
        initPaint();
        setClickable(true);
        ALog.dd(TAG, mViewName + " 构造方法被调用");
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(24);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 根据按压状态改变背景色
        if (mIsPressed) {
            canvas.drawColor(Color.parseColor("#FFD700")); // 按下时金黄色
        } else {
            canvas.drawColor(Color.parseColor("#FFF8DC")); // 正常时米色
        }

        // 绘制边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.parseColor("#FF8C00"));
        canvas.drawRect(2, 2, getWidth() - 2, getHeight() - 2, mPaint);

        // 绘制文字
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f + 8;
        canvas.drawText(mViewName, centerX, centerY, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ALog.dd(TAG, "========== " + mViewName + " onTouchEvent ==========");
        ALog.dd(TAG, mViewName + " 事件类型: " + getActionString(event.getAction()));
        ALog.dd(TAG, mViewName + " 坐标: x=" + event.getX() + ", y=" + event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsPressed = true;
                invalidate();
                ALog.dd(TAG, mViewName + " 被按下");
                break;
            case MotionEvent.ACTION_UP:
                mIsPressed = false;
                invalidate();
                ALog.dd(TAG, mViewName + " 抬起");
                break;
            case MotionEvent.ACTION_CANCEL:
                mIsPressed = false;
                invalidate();
                ALog.dd(TAG, mViewName + " 事件被取消");
                break;
        }

        ALog.dd(TAG, mViewName + " onTouchEvent 返回: true");
        ALog.dd(TAG, "========== " + mViewName + " onTouchEvent 结束 ==========");
        ALog.dd(TAG, "🔄 " + mViewName + " 向父ViewGroup汇报：事件已处理(return true)");

        return true; // 消费事件
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

    public String getViewName() {
        return mViewName;
    }
}