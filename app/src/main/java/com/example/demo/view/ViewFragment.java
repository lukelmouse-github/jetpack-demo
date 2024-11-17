package com.example.demo.view;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.drake.logcat.LogCat;
import com.drake.tooltip.ToastKt;
import com.example.common.base.BaseFragment;
import com.example.common.routers.Router;
import com.example.common.routers.RouterPath;
import com.example.common.utils.UIUtils;
import com.example.demo.R;
import com.example.demo.databinding.FragmentViewBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.VIEW, description = "自定义View页面")
public class ViewFragment extends BaseFragment<FragmentViewBinding> {

    private Handler handler = new Handler(Looper.getMainLooper());
    public ViewFragment() {
        super(R.layout.fragment_view);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.button.setOnClickListener(v -> ToastKt.toast("点击了按钮"));

        CustomSlider customSlider = new CustomSlider(getContext());
        customSlider.setMinValue(55.5f);
        customSlider.setMaxValue(95.5f);
        customSlider.setCurrentValue(60.3f);
        customSlider.setDisabled(false);
        customSlider.setBlockSize(28);
//        customSlider.setStepSize(0.2f); // 设置步长为1
        customSlider.setShowValue(true); // 显示当前值
        customSlider.setTrackColor(Color.parseColor("#88ffee")); // 设置轨道颜色
        customSlider.setSelectedTrackColor(Color.parseColor("#ff2244")); // 设置已选择轨道颜色
        customSlider.setThumbColor(Color.BLUE); // 设置滑块颜色

        customSlider.setOnValueChangingListener(value -> {
            // 在滑动过程中更新值
            // 可以在这里做一些实时的更新，比如更新 UI
            LogCat.e("当前值Changing: " + value);
        });

        customSlider.setOnValueChangeListener(value -> {
            LogCat.e("当前值Change: " + value);
        });
//        binding.main.addView(customSlider);
//        transform();
        float a = -100f; // 浮点数范围最小值
        float b = 95f; // 浮点数范围最大值
        float step = 10f; // 步长

        // 确保 (b - a) % step == 0
        int totalSteps = (int) ((b - a) / step);

        binding.buttonCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router.openFragment(RouterPath.CUSTOM_VIEW);
            }
        });

        binding.seekBar.setMax(100);
        binding.seekBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        binding.seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        // 创建自定义Thumb的Bitmap
        int thumbSize = UIUtils.dp2pxInt(40); // Thumb的大小（单位：像素）
        Bitmap customThumb = Bitmap.createBitmap(thumbSize, thumbSize, Bitmap.Config.RGB_565);
        customThumb.setHeight(thumbSize);
        customThumb.setWidth(thumbSize);
        Canvas canvas = new Canvas(customThumb);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FF4081")); // 设置Thumb的颜色
        paint.setAntiAlias(true); // 抗锯齿

        // 绘制圆形Thumb
        canvas.drawCircle(thumbSize / 2, thumbSize / 2, thumbSize / 2, paint);

        // 将Bitmap转换为Drawable
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), customThumb);

        // 设置SeekBar的Thumb
        binding.seekBar.setThumb(bitmapDrawable);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean not) {
                // todo 这里再看看。
                // i 是 整数范围内的值。
                // 将 整数 映射到 浮点数值
                // 将 progress 映射到浮点数值
                float mappedValue = a + ((float) i / 100) * (b - a);

                // 在a到b的范围内 走了几个Step
                int stepIndex = Math.round((mappedValue - a) / step);

                // 在0到100的范围内 计算最近的步长倍数值
                float nearestStepValue = stepIndex * step + a;

                // 计算 nearestStepProgress 对应的 SeekBar 进度
                int nearestStepProgress = Math.round((nearestStepValue - a) / (b - a) * 100);

                LogCat.e("原来的值 i: " + i + " mappedValue: " + mappedValue+ " nearestStepValue: " + nearestStepValue
                + " nearestStepProgress: " + nearestStepProgress);
                // 检查是否为步长的正确倍数
                if (i == nearestStepProgress) {
                    // 更新 UI
//                        textView.setText(String.format("Mapped Value: %.2f", nearestStepValue));
                    LogCat.e("Slider updated to: " + nearestStepValue);
                } else {
                    // 不更新 UI
                    // 重置进度到最近的有效步长倍数
                    seekBar.setProgress(nearestStepProgress);
//                    LogCat.e("重置了 " + mappedValue);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                LogCat.e("onStopTrackingTouch " + b);
            }
        });

        int MAX_LENGTH = 5;
    }

    private void testSlider() {
        /**
         * SeekBar 最小值为 a，最大值为 b，步长为 x，这些值都是整数
         * 现在有三个值，aa，bb，xx都是浮点数，分别代表 最小值aa，最大值bb，步长xx
         * 满足  (bb - aa) % xx == 0，
         * 能否把bb 到 aa的所有可能的值，都映射到b 到 a之间呢？
         *
         * 整数范围，n == (b - a)
         * 浮点数范围 m == (bb - aa)
         */
    }

    public static void transform() {
        int a = 0; // 整数范围最小值
        int b = 100; // 整数范围最大值
        float aa = 1.0f; // 浮点数范围最小值
        float bb = 5.0f; // 浮点数范围最大值
        float xx = 0.5f; // 步长

        // 计算浮点数值的个数
        int k = (int) ((bb - aa) / xx);

        // 映射整数到浮点数
        for (int n = 0; n <= 100; n++) {
            float mappedValue = aa + ((float) n / 100) * (bb - aa);
            if (mappedValue % xx == 0) {
                System.out.println("Integer value: " + n + " -> Mapped float value: " + mappedValue);
            }
        }
    }
}
