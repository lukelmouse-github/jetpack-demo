package com.example.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.common.log.ALog;

/**
 * 高级测量演示：展示复杂的测量场景和父子View的测量关系
 */
public class AdvancedMeasureDemo extends ViewGroup {
    private static final String TAG = "AdvancedMeasureDemo";

    private Paint mPaint;
    private int mMeasureRound = 0;

    public AdvancedMeasureDemo(Context context) {
        this(context, null);
    }

    public AdvancedMeasureDemo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdvancedMeasureDemo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ALog.dd(TAG, "AdvancedMeasureDemo 构造方法");
        initPaint();
        setWillNotDraw(false);

        // 添加几个不同类型的子View
        addTestChildren();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(24); // 增大主要文字大小
        mPaint.setColor(Color.BLACK);
    }

    private void addTestChildren() {
        // 添加一个固定大小的子View
        View fixedChild = new View(getContext()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                ALog.dd(TAG, "固定大小子View onMeasure");
                setMeasuredDimension(280, 120); // 大幅增大尺寸
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(24); // 增大字体
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setFakeBoldText(true); // 加粗字体

                // 绘制文字居中
                float centerX = getWidth() / 2f;
                float centerY = getHeight() / 2f;

                canvas.drawText("🔴 固定尺寸View", centerX, centerY - 10, textPaint);

                textPaint.setTextSize(18);
                canvas.drawText("280 x 120 dp", centerX, centerY + 20, textPaint);
            }
        };
        fixedChild.setBackgroundColor(Color.parseColor("#F44336")); // 更好看的红色
        fixedChild.setWillNotDraw(false); // 允许绘制
        addView(fixedChild);

        // 添加一个需要包裹内容的子View
        View wrapChild = new View(getContext()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                ALog.dd(TAG, "包裹内容子View onMeasure");
                ALog.dd(TAG, "父View给的约束 - 宽:" + getModeString(MeasureSpec.getMode(widthMeasureSpec))
                       + " " + MeasureSpec.getSize(widthMeasureSpec));
                ALog.dd(TAG, "父View给的约束 - 高:" + getModeString(MeasureSpec.getMode(heightMeasureSpec))
                       + " " + MeasureSpec.getSize(heightMeasureSpec));

                // 根据父View的约束计算自己的尺寸
                int width = Math.min(300, MeasureSpec.getSize(widthMeasureSpec));

                int height;
                int heightMode = MeasureSpec.getMode(heightMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);

                if (heightMode == MeasureSpec.UNSPECIFIED) {
                    // 🔥 修复：UNSPECIFIED时使用期望高度
                    height = 140; // 使用自己的期望高度
                    ALog.dd(TAG, "高度模式UNSPECIFIED，使用期望高度: " + height);
                } else {
                    height = Math.min(140, heightSize);
                    ALog.dd(TAG, "高度模式" + getModeString(heightMode) + "，计算高度: " + height);
                }

                ALog.dd(TAG, "绿色View最终尺寸: " + width + " x " + height);
                setMeasuredDimension(width, height);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(24); // 增大字体
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setFakeBoldText(true);

                // 绘制文字居中
                float centerX = getWidth() / 2f;
                float centerY = getHeight() / 2f;

                canvas.drawText("🟢 包裹内容View", centerX, centerY - 15, textPaint);

                textPaint.setTextSize(18);
                canvas.drawText("根据父View约束计算", centerX, centerY + 10, textPaint);
                canvas.drawText("最大 300 x 140 dp", centerX, centerY + 35, textPaint);
            }
        };
        wrapChild.setBackgroundColor(Color.parseColor("#4CAF50")); // 更好看的绿色
        wrapChild.setWillNotDraw(false);
        addView(wrapChild);

        // 添加一个需要填充父View的子View
        View fillChild = new View(getContext()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                ALog.dd(TAG, "填充父View子View onMeasure");

                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = 100; // 增大固定高度

                ALog.dd(TAG, "填充子View计算尺寸: " + width + " x " + height);
                setMeasuredDimension(width, height);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(24); // 增大字体
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setFakeBoldText(true);

                // 绘制文字居中
                float centerX = getWidth() / 2f;
                float centerY = getHeight() / 2f;

                canvas.drawText("🔵 填充父View宽度", centerX, centerY - 10, textPaint);

                textPaint.setTextSize(18);
                canvas.drawText("宽度填充父View，高度固定100dp", centerX, centerY + 20, textPaint);
            }
        };
        fillChild.setBackgroundColor(Color.parseColor("#2196F3")); // 更好看的蓝色
        fillChild.setWillNotDraw(false);
        addView(fillChild);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureRound++;
        ALog.dd(TAG, "========== 第" + mMeasureRound + "轮测量开始 ==========");

        // 解析父View给的约束
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        ALog.dd(TAG, "父View约束 - 宽:" + getModeString(widthMode) + " " + widthSize);
        ALog.dd(TAG, "父View约束 - 高:" + getModeString(heightMode) + " " + heightSize);

        int totalHeight = 0;
        int maxWidth = 0;

        // 逐个测量子View
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ALog.dd(TAG, "--- 开始测量第" + i + "个子View ---");

            // 创建子View的测量约束
            int childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
            int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

            ALog.dd(TAG, "给子View的约束 - 宽:" + getModeString(MeasureSpec.getMode(childWidthSpec))
                   + " " + MeasureSpec.getSize(childWidthSpec));
            ALog.dd(TAG, "给子View的约束 - 高:" + getModeString(MeasureSpec.getMode(childHeightSpec))
                   + " " + MeasureSpec.getSize(childHeightSpec));

            // 测量子View
            child.measure(childWidthSpec, childHeightSpec);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            ALog.dd(TAG, "第" + i + "个子View测量结果: " + childWidth + " x " + childHeight);

            totalHeight += childHeight;
            totalHeight += 20; // 增加子View之间的间距
            maxWidth = Math.max(maxWidth, childWidth);

            ALog.dd(TAG, "--- 第" + i + "个子View测量完成 ---");
        }

        // 计算ViewGroup自身的尺寸
        int finalWidth = resolveSize(maxWidth, widthMeasureSpec);
        int finalHeight = resolveSize(totalHeight + 40, heightMeasureSpec); // 增加顶部底部边距

        ALog.dd(TAG, "ViewGroup最终尺寸: " + finalWidth + " x " + finalHeight);
        ALog.dd(TAG, "累计子View高度: " + totalHeight + ", 最大子View宽度: " + maxWidth);

        setMeasuredDimension(finalWidth, finalHeight);

        ALog.dd(TAG, "========== 第" + mMeasureRound + "轮测量结束 ==========");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ALog.dd(TAG, "========== onLayout 开始 ==========");
        ALog.dd(TAG, "changed: " + changed);
        ALog.dd(TAG, "ViewGroup位置: (" + left + ", " + top + ", " + right + ", " + bottom + ")");

        int currentTop = 20; // 顶部留出间距

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 水平居中，垂直依次排列
            int childLeft = (getWidth() - childWidth) / 2;
            int childTop = currentTop;
            int childRight = childLeft + childWidth;
            int childBottom = childTop + childHeight;

            ALog.dd(TAG, "第" + i + "个子View布局: (" + childLeft + ", " + childTop + ", "
                   + childRight + ", " + childBottom + ")");

            child.layout(childLeft, childTop, childRight, childBottom);

            currentTop += childHeight + 20; // 增加子View之间的间距
        }

        ALog.dd(TAG, "========== onLayout 结束 ==========");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ALog.dd(TAG, "onDraw 开始");

        // 绘制背景
        canvas.drawColor(Color.parseColor("#F5F5F5"));

        // 绘制标题
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(28); // 增大标题字体
        mPaint.setFakeBoldText(true);
        canvas.drawText("📐 高级测量演示", 20, 35, mPaint);

        mPaint.setTextSize(18);
        mPaint.setFakeBoldText(false);
        canvas.drawText("💡 查看日志了解详细测量流程", 20, 65, mPaint);
        canvas.drawText("🔄 测量轮数: " + mMeasureRound, 20, 90, mPaint);

        ALog.dd(TAG, "onDraw 结束");
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

    public void resetMeasureCount() {
        mMeasureRound = 0;
        ALog.dd(TAG, "测量轮数已重置");
    }
}