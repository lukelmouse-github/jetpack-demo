package com.example.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.example.common.log.ALog;
import com.example.common.utils.UIUtils;

public class CustomSlider extends View {
    private float minValue = 0;       // 最小值
    private float maxValue = 100;     // 最大值
    private float currentValue = 0;   // 当前值
    private float stepSize = 1;       // 步长
    private boolean isDisabled = false; // 是否禁用
    private boolean showValue = false; // 是否显示当前值

    private Paint trackPaint;          // 轨道画笔
    private Paint selectedTrackPaint;  // 已选择轨道画笔
    private Paint thumbPaint;          // 滑块画笔

    private float thumbRadius = 20;    // 滑块半径
    private float trackHeight = 10;     // 轨道高度
    private float blockSize = 28;       // 滑块大小，默认值为 28

    // 颜色属性
    private int trackColor = Color.GRAY;             // 背景条颜色
    private int selectedTrackColor = Color.GREEN;    // 已选择的颜色
    private int thumbColor = Color.WHITE;             // 滑块颜色

    public CustomSlider(Context context) {
        super(context);
        init();
    }

    private void init() {
        trackPaint = new Paint();
        trackPaint.setColor(trackColor);
        trackPaint.setStrokeWidth(trackHeight);
        trackPaint.setAntiAlias(true); // 启用抗锯齿

        selectedTrackPaint = new Paint();
        selectedTrackPaint.setColor(selectedTrackColor);
        selectedTrackPaint.setStrokeWidth(trackHeight);
        selectedTrackPaint.setAntiAlias(true);

        thumbPaint = new Paint();
        thumbPaint.setColor(thumbColor);
        thumbPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTrack(canvas); // 进度条背景颜色
        drawSelectedTrack(canvas); // 已选择进度条颜色
        drawThumb(canvas); // 按钮
    }

    private void drawTrack(Canvas canvas) {
        float width = getWidth();
        float startX = blockSize / 2;
        float stopX = width - blockSize / 2;
        canvas.drawLine(startX, getHeight() / 2, stopX, getHeight() / 2, trackPaint);
    }

    private void drawSelectedTrack(Canvas canvas) {
        float selectedX = getThumbX();
        canvas.drawLine(blockSize / 2, getHeight() / 2, selectedX, getHeight() / 2, selectedTrackPaint);
    }

    private void drawThumb(Canvas canvas) {
        float thumbX = getThumbX(); // 获取x坐标
        canvas.drawCircle(thumbX, getHeight() / 2, blockSize / 2, thumbPaint); // 使用 blockSize 控制滑块大小
    }

    private float getThumbX() {
        float ratio = (currentValue - minValue) / (maxValue - minValue);
        return (blockSize / 2) + ratio * (getWidth() - blockSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDisabled) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float newValue = getValueFromX(x);
                if (newValue != currentValue) {
                    currentValue = newValue;
                    invalidate(); // 重新绘制
                    if (onValueChangingListener != null) {
                        onValueChangingListener.onValueChanging(currentValue);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (onValueChangeListener != null) {
                    onValueChangeListener.onValueChange(currentValue);
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private float getValueFromX(float x) {
        float ratio = (x - (blockSize / 2)) / (getWidth() - blockSize);
        float value = minValue + ratio * (maxValue - minValue);
        // 确保值在范围内
        value = Math.max(minValue, Math.min(value, maxValue));
        // 应用步长
        return roundToStep(value);
    }

    private float roundToStep(float value) {
        if (stepSize <= 0) {
            throw new IllegalArgumentException("步长必须大于 0");
        }
        if ((maxValue - minValue) % stepSize != 0) {
            throw new IllegalArgumentException("步长必须能够整除 (max - min)");
        }
        // 使用浮点数的步长进行四舍五入
        return Math.round(value / stepSize) * stepSize;
    }

    // Getter and Setter methods for all attributes

    public void setMinValue(float minValue) {
        this.minValue = minValue;
        invalidate();
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        invalidate();
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
        invalidate();
    }

    public void setStepSize(float stepSize) {
        if (stepSize <= 0) {
            throw new IllegalArgumentException("步长必须大于 0");
        }
        if ((maxValue - minValue) % stepSize != 0) {
            throw new IllegalArgumentException("步长必须能够整除 (max - min)");
        }
        this.stepSize = stepSize;
        invalidate();
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
        invalidate();
    }

    public void setShowValue(boolean showValue) {
        this.showValue = showValue;
        invalidate();
    }

    public void setTrackColor(int color) {
        this.trackColor = color;
        trackPaint.setColor(color);
        invalidate();
    }

    public void setSelectedTrackColor(int color) {
        this.selectedTrackColor = color;
        selectedTrackPaint.setColor(color);
        invalidate();
    }

    public void setThumbColor(int color) {
        this.thumbColor = color;
        thumbPaint.setColor(color);
        invalidate();
    }

    public void setBlockSize(float size) {
        if (size < 12 || size > 28) {
            throw new IllegalArgumentException("滑块大小必须在 12 到 28 之间");
        }
        this.blockSize = UIUtils.dp2px(size);
        ALog.e("blockSize: " + blockSize);
        invalidate();
    }

    public float getCurrentValue() {
        return currentValue;
    }

    // 监听器接口
    public interface OnValueChangeListener {
        void onValueChange(float value);
    }

    public interface OnValueChangingListener {
        void onValueChanging(float value);
    }

    private OnValueChangeListener onValueChangeListener;
    private OnValueChangingListener onValueChangingListener;

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.onValueChangeListener = listener;
    }

    public void setOnValueChangingListener(OnValueChangingListener listener) {
        this.onValueChangingListener = listener;
    }
}
