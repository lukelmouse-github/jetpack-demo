package com.example.demo.view;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.drake.logcat.LogCat;
import com.example.common.base.BaseFragment;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentCustomViewBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.CUSTOM_VIEW, description = "随便看看。。")
public class CustomViewFragment extends BaseFragment<FragmentCustomViewBinding> {

    private Handler handler = new Handler(Looper.getMainLooper());
    public CustomViewFragment() {
        super(R.layout.fragment_custom_view);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();

        MySeekBar seekBar = new MySeekBar(getContext());
        seekBar.setMax(100);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        seekBar.setBackgroundColor(Color.RED);

        MyTextView textView = new MyTextView(getContext());
        textView.setText("asdasda");
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setBackgroundColor(Color.BLUE);
        binding.container.addView(seekBar);
        binding.container.addView(textView);

        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textView.getVisibility() == View.VISIBLE) {
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.container.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // 这
                //
                // 里可以获取新的尺寸和位置
                int width = right - left;
                int height = bottom - top;
                LogCat.d("Container size changed: " + width + "x" + height);
            }
        });


        seekBar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                int width = i2 - i;
                int height = i3 - i1;
                LogCat.d("SeekBar size changed: " + width + "x" + height);
            }
        });
        textView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // 这里可以获取新的尺寸和位置
                int width = right - left;
                int height = bottom - top;
                LogCat.d("textView size changed: " + width + "x" + height);
            }
        });
    }
}
