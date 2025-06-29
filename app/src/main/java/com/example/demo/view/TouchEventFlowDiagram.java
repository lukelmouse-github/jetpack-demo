package com.example.demo.view;

/**
 * Android触摸事件分发流程图详解
 * 用ASCII图形展示完整的事件分发逻辑
 */
public class TouchEventFlowDiagram {

    /*
    ===============================================================
    🎯 图1：View层次结构
    ===============================================================
    
    ┌─────────────────────────────────────────────────────────────┐
    │                    A-ViewGroup                              │
    │  ┌─────────────────────┐    ┌─────────────────────┐        │
    │  │   B1-ViewGroup      │    │   B2-ViewGroup      │        │
    │  │ ┌─────┐   ┌─────┐   │    │ ┌─────┐   ┌─────┐   │        │
    │  │ │C11  │   │C12  │   │    │ │C21  │   │C22  │   │        │
    │  │ │View │   │View │   │    │ │View │   │View │   │        │
    │  │ └─────┘   └─────┘   │    │ └─────┘   └─────┘   │        │
    │  │         👆          │    │                     │        │ 
    │  │    点击空白区域        │    │                     │        │
    │  └─────────────────────┘    └─────────────────────┘        │
    └─────────────────────────────────────────────────────────────┘
    
    触摸坐标：(443, 105) (相对于B1的坐标)
    */

    /*
    ===============================================================
    🎯 图2：事件分发完整流程 - 点击B1空白区域
    ===============================================================
    
    Activity
        │
        ▼
    A-ViewGroup.dispatchTouchEvent()
        │
        ├─ 1. onInterceptTouchEvent() ──→ false (不拦截)
        │
        ├─ 2. 找到匹配的子View ──→ B1-ViewGroup ✅
        │
        ├─ 3. 分发给B1 ──→ B1.dispatchTouchEvent()
        │                    │
        │                    ├─ onInterceptTouchEvent() ──→ false
        │                    │  
        │                    ├─ 找子View ──→ C11❌ C12❌ (都不匹配)
        │                    │
        │                    ├─ 调用自己的onTouchEvent() ──→ false ❌
        │                    │
        │                    └─ return false ──┐
        │                                      │
        ├─ 4. 接收B1的返回值 ←──────────────────┘
        │     if (handled) return true;  // handled = false，继续执行
        │
        ├─ 5. 🔥 关键：调用自己的onTouchEvent() ──→ false ❌
        │
        └─ 6. return false ──→ Activity
    
    */

    /*
    ===============================================================
    🎯 图3：ViewGroup.dispatchTouchEvent()源码逻辑图
    ===============================================================
    
    dispatchTouchEvent(MotionEvent ev) {
        │
        ├─ boolean intercepted = onInterceptTouchEvent(ev);
        │
        ├─ if (!intercepted) {
        │    │
        │    └─ for (遍历所有子View) {
        │         │
        │         ├─ if (触摸点在子View范围内) {
        │         │    │
        │         │    ├─ boolean handled = child.dispatchTouchEvent(ev);
        │         │    │
        │         │    └─ if (handled) {
        │         │         └─ return true; 🔥 直接返回，不执行后面代码
        │         │       }
        │         │
        │         └─ break; // 找到目标子View就跳出循环
        │       }
        │
        ├─ 🔥 关键：如果执行到这里，说明没有子View处理事件
        │
        └─ return dispatchTransformedTouchEvent(ev, false, null, ...);
           │
           └─ 当child=null时，调用 super.dispatchTouchEvent(ev)
              │
              └─ 最终调用 onTouchEvent()
    }
    
    */

    /*
    ===============================================================
    🎯 图4：dispatchTransformedTouchEvent()关键方法
    ===============================================================
    
    dispatchTransformedTouchEvent(event, cancel, child, idBits) {
        │
        ├─ if (child == null) {
        │    │
        │    └─ handled = super.dispatchTouchEvent(event);
        │       │
        │       └─ 调用View.dispatchTouchEvent()
        │          │
        │          └─ 最终调用当前ViewGroup的onTouchEvent()
        │
        └─ else {
             │
             └─ handled = child.dispatchTouchEvent(event);
                │
                └─ 调用子View的dispatchTouchEvent()
           }
        
        return handled;
    }
    
    */

    /*
    ===============================================================
    🎯 图5：两种不同结果的对比
    ===============================================================
    
    情况A：子View处理了事件 (如点击C11-View)
    ┌─────────────────────────────────────────────┐
    │ A.dispatchTouchEvent()                      │
    │   ├─ 分发给B1                               │
    │   │   ├─ 分发给C11                          │
    │   │   │   └─ C11.onTouchEvent() → true ✅  │
    │   │   └─ B1收到true，直接return true ✅    │
    │   └─ A收到true，直接return true ✅         │
    │                                             │
    │ 结果：A和B1的onTouchEvent都不会被调用！     │
    └─────────────────────────────────────────────┘
    
    情况B：子View没处理事件 (如点击B1空白区域)
    ┌─────────────────────────────────────────────┐
    │ A.dispatchTouchEvent()                      │
    │   ├─ 分发给B1                               │
    │   │   ├─ 没有子View匹配                     │
    │   │   ├─ B1.onTouchEvent() → false ❌      │
    │   │   └─ B1 return false ❌                │
    │   ├─ A收到false，继续执行                   │
    │   ├─ A.onTouchEvent() → false ❌           │
    │   └─ A return false ❌                     │
    │                                             │
    │ 结果：责任链向上传递！                      │
    └─────────────────────────────────────────────┘
    
    */

    /*
    ===============================================================
    🎯 图6：关键代码在源码中的位置
    ===============================================================
    
    ViewGroup.java (大约2000-2100行)
    
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // ... 前面的代码 ...
        
        // 🔥 关键部分1：子View处理逻辑
        if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) {
            // Child wants to receive touch within its bounds.
            mLastTouchDownTime = ev.getDownTime();
            newTouchTarget = addTouchTarget(child, idBitsToAssign);
            alreadyDispatchedToNewTouchTarget = true;
            break; // 🔥 跳出循环，不会执行后面的自己处理逻辑
        }
        
        // ... 中间的代码 ...
        
        // 🔥 关键部分2：自己处理逻辑  
        if (mFirstTouchTarget == null) {
            // No touch targets so treat this as an ordinary view.
            handled = dispatchTransformedTouchEvent(ev, canceled, null,
                    TouchTarget.ALL_POINTER_IDS);
            //     ☝️ child=null，会调用自己的onTouchEvent
        }
        
        return handled;
    }
    
    */

    /*
    ===============================================================
    🎯 图7：实际运行时的调用栈
    ===============================================================
    
    当点击B1空白区域时的调用栈：
    
    1️⃣ A-ViewGroup.dispatchTouchEvent()
        ├─ A-ViewGroup.onInterceptTouchEvent() → false
        ├─ 找到B1-ViewGroup匹配
        └─ 调用 B1-ViewGroup.dispatchTouchEvent()
    
    2️⃣ B1-ViewGroup.dispatchTouchEvent()
        ├─ B1-ViewGroup.onInterceptTouchEvent() → false  
        ├─ 遍历子View：C11❌ C12❌ (都不匹配)
        ├─ 调用 B1-ViewGroup.onTouchEvent() → false
        └─ return false
    
    3️⃣ 回到 A-ViewGroup.dispatchTouchEvent()
        ├─ 收到B1返回的false
        ├─ if (handled) return true; // handled=false，跳过
        ├─ 🔥 执行到最后：dispatchTransformedTouchEvent(ev, false, null, ...)
        ├─ 这会调用 A-ViewGroup.onTouchEvent() → false
        └─ return false
    
    4️⃣ Activity收到false
        └─ 没有View处理这个事件
    
    */
}