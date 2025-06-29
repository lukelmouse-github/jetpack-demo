package com.example.demo.view;

/**
 * onInterceptTouchEvent调用逻辑详解
 * 解释什么时候会调用，什么时候不会调用
 */
public class InterceptLogicExplain {

    /*
    ===============================================================
    🛡️ onInterceptTouchEvent的调用时机详解
    ===============================================================

    ❌ 错误认知：ViewGroup总是先调用onInterceptTouchEvent()
    ✅ 正确理解：只有特定条件下才会调用onInterceptTouchEvent()

    */

    // ===============================================================
    // 📋 Android源码中的真实逻辑
    // ===============================================================
    
    private void realSourceCodeLogic() {
        /*
        // ViewGroup.dispatchTouchEvent()中的关键代码
        
        final boolean intercepted;
        if (actionMasked == MotionEvent.ACTION_DOWN || mFirstTouchTarget != null) {
            final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
            if (!disallowIntercept) {
                intercepted = onInterceptTouchEvent(ev);  // 🔥 只有这里才调用！
                ev.setAction(action); // restore action in case it was changed
            } else {
                intercepted = false;
            }
        } else {
            // There are no touch targets and this action is not an initial down
            // so this view group continues to intercept touches.
            intercepted = true;  // 🔥 直接设为true，不调用onInterceptTouchEvent！
        }
        */
    }

    // ===============================================================
    // 🎯 调用条件分析
    // ===============================================================

    /**
     * 条件1：ACTION_DOWN事件
     */
    private void condition1_ActionDown() {
        /*
        用户刚开始触摸屏幕时：
        
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // ✅ 一定会检查onInterceptTouchEvent()
            intercepted = onInterceptTouchEvent(ev);
        }
        
        原因：
        • DOWN是触摸手势的开始
        • 需要决定整个手势由谁处理
        • 这是拦截的最佳时机
        */
    }

    /**
     * 条件2：有子View正在处理事件（mFirstTouchTarget != null）
     */
    private void condition2_HasTouchTarget() {
        /*
        当有子View正在处理事件时：
        
        if (mFirstTouchTarget != null) {
            // ✅ 会检查onInterceptTouchEvent()
            intercepted = onInterceptTouchEvent(ev);
        }
        
        mFirstTouchTarget是什么？
        • 指向正在处理触摸事件的子View链表
        • 当子View消费了DOWN事件后，就会被加入这个链表
        • MOVE/UP事件期间，可能会发生"中途拦截"
        
        原因：
        • 父ViewGroup可能在中途改变主意想要拦截事件
        • 比如ScrollView在检测到滑动手势后拦截事件
        */
    }

    /**
     * 条件3：不是DOWN且没有TouchTarget
     */
    private void condition3_NoTouchTarget() {
        /*
        当不是DOWN事件且没有子View在处理时：
        
        if (actionMasked != MotionEvent.ACTION_DOWN && mFirstTouchTarget == null) {
            // ❌ 直接设置intercepted = true，不调用onInterceptTouchEvent()
            intercepted = true;
        }
        
        这种情况的含义：
        • 前面的DOWN事件没有任何子View处理
        • 后续的MOVE/UP事件肯定也不会有子View处理
        • 直接认为父ViewGroup拦截了，无需再调用onInterceptTouchEvent()
        
        这是一种性能优化！
        */
    }

    // ===============================================================
    // 🚫 FLAG_DISALLOW_INTERCEPT的影响
    // ===============================================================

    private void disallowInterceptFlag() {
        /*
        子View可以通过requestDisallowInterceptTouchEvent(true)禁止父ViewGroup拦截：
        
        if (!disallowIntercept) {
            intercepted = onInterceptTouchEvent(ev);  // 正常调用
        } else {
            intercepted = false;  // 强制不拦截，不调用onInterceptTouchEvent()
        }
        
        使用场景：
        • 子View正在处理复杂手势（如滑动）
        • 不希望父ViewGroup中途拦截
        • 常见于ScrollView嵌套场景
        */
    }

    // ===============================================================
    // 📊 各种场景下的调用情况
    // ===============================================================

    /**
     * 场景1：用户点击子View
     */
    private void scenario1_ClickChild() {
        /*
        DOWN事件：
        ✅ 调用onInterceptTouchEvent() → false → 分发给子View → 子View处理

        MOVE事件：
        ✅ 调用onInterceptTouchEvent() → false → 继续给子View
        (因为mFirstTouchTarget != null)

        UP事件：
        ✅ 调用onInterceptTouchEvent() → false → 继续给子View
        */
    }

    /**
     * 场景2：用户点击空白区域
     */
    private void scenario2_ClickEmpty() {
        /*
        DOWN事件：
        ✅ 调用onInterceptTouchEvent() → 分发给子View → 无子View处理

        MOVE事件：
        ❌ 不调用onInterceptTouchEvent()，直接intercepted = true
        (因为mFirstTouchTarget == null)

        UP事件：
        ❌ 不调用onInterceptTouchEvent()，直接intercepted = true
        */
    }

    /**
     * 场景3：父ViewGroup主动拦截
     */
    private void scenario3_ParentIntercept() {
        /*
        DOWN事件：
        ✅ 调用onInterceptTouchEvent() → true → 父ViewGroup直接处理

        MOVE事件：
        ❌ 不调用onInterceptTouchEvent()，直接intercepted = true
        (因为mFirstTouchTarget == null，没有子View在处理)

        UP事件：
        ❌ 不调用onInterceptTouchEvent()，直接intercepted = true
        */
    }

    /**
     * 场景4：中途拦截（如ScrollView滑动检测）
     */
    private void scenario4_MidwayIntercept() {
        /*
        DOWN事件：
        ✅ 调用onInterceptTouchEvent() → false → 子View处理

        MOVE事件：
        ✅ 调用onInterceptTouchEvent() → true → 拦截！发送CANCEL给子View
        (ScrollView检测到滑动手势，决定拦截)

        后续MOVE/UP：
        ❌ 不再调用onInterceptTouchEvent()，直接intercepted = true
        (因为已经拦截了，mFirstTouchTarget被清空)
        */
    }

    // ===============================================================
    // 🎯 关键理解点
    // ===============================================================

    /*
    1. onInterceptTouchEvent()不是每次都调用的！
    
    2. 只有以下情况才调用：
       • ACTION_DOWN事件
       • 有子View正在处理事件的非DOWN事件
       • 且没有被子View禁止拦截
    
    3. 性能优化：
       • 当确定没有子View处理时，直接跳过拦截检查
       • 避免不必要的方法调用
    
    4. 中途拦截机制：
       • 即使DOWN事件不拦截，MOVE事件仍可拦截
       • 这是ScrollView等组件的核心机制
    
    5. 子View保护机制：
       • requestDisallowInterceptTouchEvent()可以禁止父ViewGroup拦截
       • 用于复杂的嵌套滑动场景
    */
}