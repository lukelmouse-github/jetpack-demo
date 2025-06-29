package com.example.demo.view;

/**
 * TextView源码分析：为什么同时调用requestLayout()和invalidate()
 * 深入解析Android View系统的细节机制
 */
public class TextViewSourceAnalysis {

    /*
    ===============================================================
    🎯 核心问题：为什么TextView同时调用两个方法？
    ===============================================================
    
    TextView.setText()源码中的关键调用：
    
    private void setText(CharSequence text, BufferType type, boolean notifyBefore, int oldlen) {
        // ... 设置文字内容 ...
        
        if (mLayout != null) {
            checkForRelayout(); // 🔥 这里会调用requestLayout()
        }
        
        if (hasPasswordTransformationMethod()) {
            invalidate(); // 🔥 同时调用invalidate()
        }
        
        // 或者在其他情况下
        requestLayout();  // 🔥 处理尺寸变化
        invalidate();     // 🔥 确保内容重绘
    }
    
    为什么需要两个？让我们分析具体场景...
    
    */

    /*
    ===============================================================
    📊 场景分析：requestLayout()的draw执行条件
    ===============================================================
    
    场景1：文字变长，尺寸改变
    ┌─────────────────────────────────────────┐
    │ TextView.setText("很长的文字内容...")      │
    │ ├─ requestLayout()                      │
    │ │  ├─ onMeasure() → 新尺寸 400x60       │
    │ │  ├─ onLayout() → 位置可能变化          │
    │ │  └─ onDraw() ✅ 尺寸变了,自动重绘     │
    │ └─ invalidate() → 多余但无害             │
    └─────────────────────────────────────────┘
    
    场景2：文字内容变化，尺寸不变
    ┌─────────────────────────────────────────┐
    │ TextView.setText("ABC" → "XYZ")         │
    │ ├─ requestLayout()                      │
    │ │  ├─ onMeasure() → 相同尺寸 100x40     │
    │ │  ├─ onLayout() → 相同位置              │
    │ │  └─ onDraw() ❌ 尺寸没变,可能跳过!    │
    │ └─ invalidate() ✅ 确保内容重绘         │
    └─────────────────────────────────────────┘
    
    场景3：wrap_content但内容变短
    ┌─────────────────────────────────────────┐
    │ TextView.setText("Long" → "Hi")         │
    │ ├─ requestLayout()                      │
    │ │  ├─ onMeasure() → 更小尺寸 50x40      │
    │ │  ├─ onLayout() → 位置可能变化          │
    │ │  └─ onDraw() ✅ 尺寸变了,自动重绘     │
    │ └─ invalidate() → 双保险                │
    └─────────────────────────────────────────┘
    
    */

    /*
    ===============================================================
    🔬 ViewRootImpl.performTraversals()详细逻辑
    ===============================================================
    
    // Android源码中的关键判断逻辑
    private void performTraversals() {
        
        boolean layoutRequested = mLayoutRequested && (!mStopped || mReportNextDraw);
        boolean windowShouldResize = layoutRequested && windowSizeMayChange;
        
        // 🔄 执行测量和布局
        if (layoutRequested) {
            performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
            performLayout(lp, mWidth, mHeight);
        }
        
        // 🎨 绘制执行条件判断
        boolean triggerGlobalLayoutListener = layoutRequested;
        boolean canDrawNow = true;
        
        // 🔥 关键判断：什么时候需要重绘？
        if (layoutRequested) {
            // 检查View树是否真的发生了变化
            if (mFirst || windowShouldResize || insetsChanged || 
                viewVisibilityChanged || params != null) {
                mFullRedrawNeeded = true; // 需要完整重绘
            }
        }
        
        // 🔥 只有满足条件才执行绘制
        if (!mStopped && (mFullRedrawNeeded || newSurface)) {
            performDraw(); // 这里才真正绘制
        }
    }
    
    关键问题：如果View的尺寸没有发生变化，
    requestLayout()可能不会触发onDraw()！
    
    */

    /*
    ===============================================================
    🎯 TextView具体源码分析
    ===============================================================
    
    // TextView.checkForRelayout()方法
    private void checkForRelayout() {
        if ((mLayoutParams.width != LayoutParams.WRAP_CONTENT ||
             (mMaxWidthMode == mMinWidthMode && mMaxWidth == mMinWidth)) &&
             (mHint == null || mHintLayout != null) &&
             (mRight - mLeft - getCompoundPaddingLeft() - getCompoundPaddingRight() > 0)) {
                 
            // 🔥 情况1：固定宽度，只需要检查高度
            int oldht = mLayout.getHeight();
            makeNewLayout(want, hintWant, UNKNOWN_BORING, UNKNOWN_BORING, 
                         mRight - mLeft - getCompoundPaddingLeft() - getCompoundPaddingRight(), 
                         false);
                         
            // 高度变化了才requestLayout
            if (mLayout.getHeight() != oldht) {
                requestLayout();
                invalidate(); // 🔥 同时调用确保重绘
            }
        } else {
            // 🔥 情况2：wrap_content，可能需要重新测量
            nullLayouts();
            requestLayout();  // 🔥 可能改变尺寸
            invalidate();     // 🔥 确保内容更新
        }
    }
    
    */

    /*
    ===============================================================
    ⚡ 实际测试验证
    ===============================================================
    
    测试场景：TextView固定尺寸，只改变文字内容
    
    // 测试代码
    TextView textView = findViewById(R.id.textView);
    textView.setWidth(200); // 固定宽度
    textView.setHeight(60); // 固定高度
    
    // 情况A：只调用requestLayout()
    textView.setText("New Text");
    textView.requestLayout(); // 可能不会重绘内容！
    
    // 情况B：只调用invalidate()  
    textView.setText("New Text");
    textView.invalidate(); // 内容更新但可能位置不对
    
    // 情况C：同时调用（TextView的做法）
    textView.setText("New Text");
    textView.requestLayout(); // 处理可能的尺寸变化
    textView.invalidate();    // 确保内容重绘
    
    结果：只有情况C能保证在所有情况下都正确显示！
    
    */

    /*
    ===============================================================
    🛡️ 防御性编程的考虑
    ===============================================================
    
    TextView同时调用两个方法的原因：
    
    1. 🎯 覆盖所有情况：
       • requestLayout()处理尺寸可能变化的情况
       • invalidate()确保内容一定会重绘
    
    2. 🚀 性能vs安全性的权衡：
       • 多调用一次invalidate()的开销很小
       • 但能避免UI显示错误的严重问题
    
    3. 🔄 系统复杂性：
       • Android设备众多，ROM定制各异
       • 防御性编程确保兼容性
    
    4. 📱 用户体验优先：
       • 宁可多执行一次绘制
       • 也不能让用户看到错误的内容
    
    */

    /*
    ===============================================================
    💡 最佳实践建议
    ===============================================================
    
    对于开发者：
    
    1. ✅ 理解原理，按需使用：
       // 只改变外观
       view.setBackgroundColor(Color.RED);
       view.invalidate(); // 只需invalidate
       
       // 改变尺寸
       ViewGroup.LayoutParams params = view.getLayoutParams();
       params.width = newWidth;
       view.setLayoutParams(params); // 内部会调用requestLayout
       
       // 复杂变化（不确定是否影响尺寸）
       customView.setComplexData(data);
       customView.requestLayout(); // 处理可能的尺寸变化
       customView.invalidate();    // 确保内容更新
    
    2. 🎯 自定义View的建议：
       • 明确知道只影响外观：只用invalidate()
       • 明确知道影响尺寸：只用requestLayout()
       • 不确定或复杂情况：同时使用（学习TextView）
    
    3. ⚡ 性能考虑：
       • invalidate()很轻量，多调用一次无害
       • requestLayout()较重，不要滥用
       • 批量更新时在最后统一调用
    
    */

    /*
    ===============================================================
    📋 总结
    ===============================================================
    
    TextView同时调用requestLayout()和invalidate()的原因：
    
    🔥 核心原因：
    • requestLayout()的draw阶段有执行条件
    • 当尺寸没变时，可能不会重绘内容
    • invalidate()确保内容一定会更新
    
    🛡️ 设计理念：
    • 防御性编程，确保所有情况都正确
    • 用户体验优先于性能微优化
    • 系统组件需要极高的稳定性
    
    💡 启示：
    • 理解Android系统的复杂性和细致考虑
    • 在自定义组件中也应该考虑边界情况
    • 简单的API背后往往有复杂的实现逻辑
    
    这就是为什么Android系统如此稳定的原因之一！
    
    */
}