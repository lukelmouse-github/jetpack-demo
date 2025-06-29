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

import com.drake.tooltip.ToastKt;
import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.Router;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentViewBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.VIEW, description = "自定义View页面")
public class ViewFragment extends BaseFragment<FragmentViewBinding> {
    public ViewFragment() {
        super(R.layout.fragment_view);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
