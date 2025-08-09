package com.example.demo.textview;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;

import com.example.common.base.BaseFragment;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.example.demo.R;
import com.example.demo.databinding.FragmentTextviewNativeBinding;
import com.therouter.router.Route;

@Route(path = RouterPath.TEXTVIEW_NATIVE, description = "TextView Native自动化")
public class TextViewNativeFragment extends BaseFragment<FragmentTextviewNativeBinding> {
    private static final String TAG = "TextViewNativeFragment";
    
    private EditText etMainInput;
    private TextView tvValueSelector;
    
    // Value选项
    private String[] valueOptions = {
        "第一行\n第二行\n第三行",
        "单行文本",
        "多行文本示例\n第二行内容\n第三行内容",
        ""
    };
    private int currentValueIndex = 0;

    public TextViewNativeFragment() {
        super(R.layout.fragment_textview_native);
    }

    @Override
    protected void initView() {
        super.initView();
        ALog.dd(TAG, "TextViewNativeFragment initView开始");
        
        // 初始化视图
        etMainInput = binding.etMainInput;
        tvValueSelector = binding.tvValueSelector;
        
        // 设置初始值
        updateEditTextValue();
        
        // 设置Value选择器点击事件
        tvValueSelector.setOnClickListener(v -> showValueSelector());
        
        // 其他选择器也设置点击事件（占位用）
        binding.tvPlaceholderSelector.setOnClickListener(v -> showGenericSelector("Placeholder"));
        binding.tvDisabledSelector.setOnClickListener(v -> showGenericSelector("Disabled"));
        binding.tvMaxlengthSelector.setOnClickListener(v -> showGenericSelector("Maxlength"));
        binding.tvAutofocusSelector.setOnClickListener(v -> showGenericSelector("AutoFocus"));
        binding.tvFocusSelector.setOnClickListener(v -> showGenericSelector("Focus"));
        binding.tvAutoheightSelector.setOnClickListener(v -> showGenericSelector("AutoHeight"));
        binding.tvFixedSelector.setOnClickListener(v -> showGenericSelector("Fixed"));
        binding.tvCursorSpacingSelector.setOnClickListener(v -> showGenericSelector("CursorSpacing"));
        binding.tvShowConfirmBarSelector.setOnClickListener(v -> showGenericSelector("ShowConfirmBar"));
        binding.tvSelectionStartSelector.setOnClickListener(v -> showGenericSelector("SelectionStart"));
        binding.tvSelectionEndSelector.setOnClickListener(v -> showGenericSelector("SelectionEnd"));
        binding.tvAdjustPositionSelector.setOnClickListener(v -> showGenericSelector("AdjustPosition"));
        binding.tvHoldKeyboardSelector.setOnClickListener(v -> showGenericSelector("HoldKeyboard"));
        binding.tvCursorSelector.setOnClickListener(v -> showGenericSelector("Cursor"));
        binding.tvShowCountSelector.setOnClickListener(v -> showGenericSelector("ShowCount"));
        binding.tvEnableNativeSelector.setOnClickListener(v -> showGenericSelector("EnableNative"));
        binding.tvAutoBlurSelector.setOnClickListener(v -> showGenericSelector("AutoBlur"));
        binding.tvIgnoreCompositionSelector.setOnClickListener(v -> showGenericSelector("IgnoreComposition"));
        binding.tvAlwaysEmbedSelector.setOnClickListener(v -> showGenericSelector("AlwaysEmbed"));
        binding.tvAlwaysSystemSelector.setOnClickListener(v -> showGenericSelector("AlwaysSystem"));
        binding.tvBindKeyboardSelector.setOnClickListener(v -> showGenericSelector("BindKeyboard"));
        binding.tvConfirmTypeSelector.setOnClickListener(v -> showGenericSelector("ConfirmType"));
        binding.tvConfirmHoldSelector.setOnClickListener(v -> showGenericSelector("ConfirmHold"));
        
        ALog.dd(TAG, "TextViewNativeFragment initView完成");
    }
    
    private void showValueSelector() {
        String[] displayOptions = {
            "换行-第一行第二行第三行",
            "单行文本",
            "多行文本示例",
            "空内容"
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择Value值")
                .setSingleChoiceItems(displayOptions, currentValueIndex, (dialog, which) -> {
                    currentValueIndex = which;
                    updateEditTextValue();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void updateEditTextValue() {
        String selectedValue = valueOptions[currentValueIndex];
        etMainInput.setText(selectedValue);
    }
    
    private void showGenericSelector(String selectorName) {
        // 占位选择器，显示简单的提示
        String[] options = {"选项1", "选项2", "选项3"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择" + selectorName + "值")
                .setItems(options, (dialog, which) -> {
                    // 这里可以添加具体的处理逻辑
                    dialog.dismiss();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void initData() {
        super.initData();
        ALog.dd(TAG, "TextViewNativeFragment initData");
    }
}

