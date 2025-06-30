package com.example.demo.textview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentTextviewHintBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.TEXTVIEW_HINT, description = "TextView Hint机制深度解析")
public class TextViewHintFragment extends BaseFragment<FragmentTextviewHintBinding> {
    private static final String TAG = "TextViewHint";

    public TextViewHintFragment() {
        super(R.layout.fragment_textview_hint);
    }

    @Override
    protected void initView() {
        super.initView();
        ALog.dd(TAG, "TextViewHintFragment initView开始");

        // 创建标题
        TextView titleView = new TextView(getContext());
        titleView.setText("🔍 TextView Hint机制深度解析");
        titleView.setTextSize(22);
        titleView.setTextColor(Color.BLACK);
        titleView.setPadding(0, 0, 0, 20);
        titleView.setGravity(android.view.Gravity.CENTER);
        binding.main.addView(titleView);

        // 添加理论说明
        addTheorySection();
        
        // 添加基础hint演示
        addBasicHintDemo();
        
        // 添加hint显示逻辑演示
        addHintLogicDemo();
        
        // 添加hint绘制原理演示
        addHintDrawDemo();
        
        // 添加自定义hint演示
        addCustomHintDemo();

        ALog.dd(TAG, "TextViewHintFragment initView完成");
    }

    private void addTheorySection() {
        TextView theoryTitle = new TextView(getContext());
        theoryTitle.setText("📚 Hint机制核心原理");
        theoryTitle.setTextSize(18);
        theoryTitle.setTextColor(Color.BLACK);
        theoryTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(theoryTitle);

        TextView theoryContent = new TextView(getContext());
        theoryContent.setText(
                "1️⃣ Hint显示判断逻辑：\n" +
                "   • getText().length() == 0 时显示hint\n" +
                "   • 焦点状态影响hint透明度\n" +
                "   • hint文本为null时不绘制\n\n" +
                
                "2️⃣ Hint绘制时机：\n" +
                "   • onDraw()方法中调用drawHint()\n" +
                "   • 在主文本绘制之前绘制hint\n" +
                "   • 使用mHintLayout进行文本布局\n\n" +
                
                "3️⃣ Hint样式控制：\n" +
                "   • textColorHint属性控制颜色\n" +
                "   • 继承主文本的字体和大小\n" +
                "   • 支持SpannableString富文本hint\n\n" +
                
                "4️⃣ Hint与焦点交互：\n" +
                "   • 获得焦点时hint变淡\n" +
                "   • 失去焦点时hint恢复正常\n" +
                "   • 动画过渡效果实现"
        );
        theoryContent.setTextSize(14);
        theoryContent.setTextColor(Color.DKGRAY);
        theoryContent.setPadding(10, 0, 10, 20);
        theoryContent.setLineSpacing(6, 1.2f);
        theoryContent.setBackgroundColor(Color.parseColor("#F8F9FA"));
        binding.main.addView(theoryContent);
    }

    private void addBasicHintDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🎯 基础Hint演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        // 普通hint
        EditText normalHint = new EditText(getContext());
        normalHint.setHint("这是普通的hint文本");
        normalHint.setHintTextColor(Color.GRAY);
        normalHint.setPadding(20, 15, 20, 15);
        normalHint.setBackgroundColor(Color.parseColor("#F0F0F0"));
        binding.main.addView(normalHint);

        // 多行hint
        EditText multilineHint = new EditText(getContext());
        multilineHint.setHint("这是多行hint文本\n第二行hint\n第三行hint");
        multilineHint.setHintTextColor(Color.parseColor("#FF6B6B"));
        multilineHint.setSingleLine(false);
        multilineHint.setLines(3);
        multilineHint.setPadding(20, 15, 20, 15);
        multilineHint.setBackgroundColor(Color.parseColor("#FFF0F0"));
        LinearLayout.LayoutParams multiParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        multiParams.setMargins(0, 10, 0, 0);
        multilineHint.setLayoutParams(multiParams);
        binding.main.addView(multilineHint);
    }

    private void addHintLogicDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🔬 Hint显示逻辑分析");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        // 创建分析用的EditText
        EditText analysisEdit = new EditText(getContext());
        analysisEdit.setHint("输入文本观察hint变化");
        analysisEdit.setHintTextColor(Color.BLUE);
        analysisEdit.setPadding(20, 15, 20, 15);
        analysisEdit.setBackgroundColor(Color.parseColor("#E8F4FD"));
        binding.main.addView(analysisEdit);

        // 状态显示TextView
        TextView statusView = new TextView(getContext());
        statusView.setText("状态：hint显示中");
        statusView.setTextSize(14);
        statusView.setTextColor(Color.BLACK);
        statusView.setPadding(10, 10, 10, 10);
        statusView.setBackgroundColor(Color.parseColor("#FFFACD"));
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        statusParams.setMargins(0, 10, 0, 0);
        statusView.setLayoutParams(statusParams);
        binding.main.addView(statusView);

        // 添加文本变化监听
        analysisEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ALog.dd(TAG, "beforeTextChanged: s=" + s + ", start=" + start + ", count=" + count + ", after=" + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ALog.dd(TAG, "onTextChanged: s=" + s + ", start=" + start + ", before=" + before + ", count=" + count);
                
                boolean shouldShowHint = TextUtils.isEmpty(s);
                String status = shouldShowHint ? "hint显示中" : "hint隐藏中";
                statusView.setText("状态：" + status + " | 文本长度：" + s.length() + " | 内容：\"" + s + "\"");
                
                ALog.dd(TAG, "hint显示判断: shouldShowHint=" + shouldShowHint + ", textLength=" + s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                ALog.dd(TAG, "afterTextChanged: s=" + s);
            }
        });

        // 添加焦点变化监听
        analysisEdit.setOnFocusChangeListener((v, hasFocus) -> {
            ALog.dd(TAG, "焦点变化: hasFocus=" + hasFocus);
            String focusStatus = hasFocus ? "获得焦点" : "失去焦点";
            statusView.setText(statusView.getText() + " | " + focusStatus);
        });
    }

    private void addHintDrawDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("🎨 Hint绘制原理演示");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        // 自定义绘制hint的TextView
        CustomHintTextView customHintView = new CustomHintTextView(getContext());
        customHintView.setHint("自定义绘制的hint");
        customHintView.setHintTextColor(Color.parseColor("#FF9800"));
        customHintView.setPadding(20, 15, 20, 15);
        customHintView.setBackgroundColor(Color.parseColor("#FFF8E1"));
        customHintView.setTextSize(16);
        LinearLayout.LayoutParams customParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        customParams.setMargins(0, 10, 0, 0);
        customHintView.setLayoutParams(customParams);
        binding.main.addView(customHintView);

        // 添加控制按钮
        Button toggleButton = new Button(getContext());
        toggleButton.setText("切换绘制模式");
        toggleButton.setOnClickListener(v -> {
            customHintView.toggleDrawMode();
            ALog.dd(TAG, "切换hint绘制模式");
        });
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, 10, 0, 0);
        buttonParams.gravity = android.view.Gravity.CENTER;
        toggleButton.setLayoutParams(buttonParams);
        binding.main.addView(toggleButton);
    }

    private void addCustomHintDemo() {
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText("✨ 自定义Hint效果");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(Color.BLACK);
        sectionTitle.setPadding(0, 20, 0, 10);
        binding.main.addView(sectionTitle);

        // 浮动hint效果
        FloatingHintEditText floatingHint = new FloatingHintEditText(getContext());
        floatingHint.setHint("Material Design浮动hint");
        floatingHint.setHintTextColor(Color.parseColor("#4CAF50"));
        floatingHint.setPadding(20, 30, 20, 15);
        floatingHint.setBackgroundColor(Color.parseColor("#F1F8E9"));
        LinearLayout.LayoutParams floatingParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        floatingParams.setMargins(0, 10, 0, 20);
        floatingHint.setLayoutParams(floatingParams);
        binding.main.addView(floatingHint);
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "TextViewHintFragment initData");
    }

    // 自定义TextView，演示hint绘制原理
    public static class CustomHintTextView extends androidx.appcompat.widget.AppCompatEditText {
        private boolean customDrawMode = false;
        private Paint hintPaint;

        public CustomHintTextView(Context context) {
            super(context);
            init();
        }

        public CustomHintTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            hintPaint = new Paint();
            hintPaint.setAntiAlias(true);
            hintPaint.setTextSize(getTextSize());
        }

        public void toggleDrawMode() {
            customDrawMode = !customDrawMode;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            ALog.dd("CustomHintTextView", "onDraw开始 - customDrawMode=" + customDrawMode);
            
            if (customDrawMode && TextUtils.isEmpty(getText()) && !TextUtils.isEmpty(getHint())) {
                // 自定义绘制hint
                ALog.dd("CustomHintTextView", "自定义绘制hint: " + getHint());
                
                hintPaint.setColor(getCurrentHintTextColor());
                hintPaint.setTextSize(getTextSize());
                
                // 绘制hint文本
                String hintText = getHint().toString();
                float x = getPaddingLeft();
                float y = getPaddingTop() + getTextSize();
                
                canvas.drawText(hintText, x, y, hintPaint);
                
                // 绘制边框表示这是自定义绘制
                hintPaint.setColor(Color.RED);
                hintPaint.setStyle(Paint.Style.STROKE);
                hintPaint.setStrokeWidth(2);
                canvas.drawRect(getPaddingLeft() - 5, getPaddingTop() - 5, 
                               getWidth() - getPaddingRight() + 5, 
                               getHeight() - getPaddingBottom() + 5, hintPaint);
                hintPaint.setStyle(Paint.Style.FILL);
            } else {
                // 使用系统默认绘制
                ALog.dd("CustomHintTextView", "使用系统默认绘制hint");
                super.onDraw(canvas);
            }
            
            ALog.dd("CustomHintTextView", "onDraw完成");
        }
    }

    // 浮动hint效果的EditText
    public static class FloatingHintEditText extends androidx.appcompat.widget.AppCompatEditText {
        private Paint floatingHintPaint;
        private ValueAnimator hintAnimator;
        private float hintOffset = 0f;
        private boolean isFloating = false;

        public FloatingHintEditText(Context context) {
            super(context);
            init();
        }

        public FloatingHintEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            floatingHintPaint = new Paint();
            floatingHintPaint.setAntiAlias(true);
            floatingHintPaint.setTextSize(getTextSize() * 0.8f);
            
            addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    boolean shouldFloat = !TextUtils.isEmpty(s);
                    if (shouldFloat != isFloating) {
                        animateHint(shouldFloat);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        private void animateHint(boolean shouldFloat) {
            ALog.dd("FloatingHintEditText", "animateHint: shouldFloat=" + shouldFloat);
            
            if (hintAnimator != null) {
                hintAnimator.cancel();
            }
            
            float startOffset = hintOffset;
            float endOffset = shouldFloat ? -getTextSize() * 0.8f : 0f;
            
            hintAnimator = ValueAnimator.ofFloat(startOffset, endOffset);
            hintAnimator.setDuration(200);
            hintAnimator.addUpdateListener(animation -> {
                hintOffset = (float) animation.getAnimatedValue();
                invalidate();
            });
            hintAnimator.start();
            
            isFloating = shouldFloat;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            
            if (!TextUtils.isEmpty(getHint())) {
                floatingHintPaint.setColor(getCurrentHintTextColor());
                
                String hintText = getHint().toString();
                float x = getPaddingLeft();
                float y = getPaddingTop() + getTextSize() + hintOffset;
                
                if (isFloating) {
                    // 浮动状态下的hint
                    floatingHintPaint.setTextSize(getTextSize() * 0.7f);
                    canvas.drawText(hintText, x, y, floatingHintPaint);
                    ALog.dd("FloatingHintEditText", "绘制浮动hint: " + hintText + ", offset=" + hintOffset);
                }
            }
        }
    }
}