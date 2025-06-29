package com.example.demo.view;

/**
 * ViewGroup触摸事件分发的最简化伪代码
 * 这是纯粹的逻辑描述，不是可执行代码
 */
public class SimpleDispatchLogic {

    /*
    ========== ViewGroup.dispatchTouchEvent() 伪代码 ==========

    public boolean dispatchTouchEvent(MotionEvent event) {

        // 第1步：检查是否要拦截事件
        if (onInterceptTouchEvent(event)) {
            // 如果要拦截，直接自己处理
            return onTouchEvent(event);
        }

        // 第2步：寻找合适的子View
        View targetChild = findTargetChild(event.x, event.y);

        if (targetChild != null) {
            // 第3步：分发给找到的子View
            boolean handled = targetChild.dispatchTouchEvent(event);
            if (handled) {
                return true; // 子View处理了事件
            }
        }

        // 第4步：没有子View处理，自己处理
        return onTouchEvent(event);
    }

    ========== 寻找目标子View的算法 ==========

    private View findTargetChild(float x, float y) {
        // 从后往前遍历子View（后添加的在上层，Z轴顺序）
        for (int i = childCount - 1; i >= 0; i--) {
            View child = children[i];

            // 检查触摸点是否在子View范围内
            if (pointInChild(x, y, child)) {
                return child; // 找到目标子View
            }
        }
        return null; // 没找到，说明点击的是空白区域
    }

    ========== 坐标匹配算法 ==========

    private boolean pointInChild(float x, float y, View child) {
        return x >= child.left
            && x < child.right
            && y >= child.top
            && y < child.bottom;
    }

    */
}

/*
========== 总结：ViewGroup事件分发的4个步骤 ==========

1. 检查拦截
   if (要拦截) → 自己处理

2. 找子View
   遍历所有子View，找到触摸点对应的那个

3. 分发事件
   调用子View.dispatchTouchEvent()

4. 兜底处理
   如果没有子View处理，自己处理

========== 核心原则 ==========
• 从后往前遍历（Z轴顺序）
• 坐标匹配算法：点在矩形内
• 责任链模式：找不到就往上传
• 拦截机制：父View可以截胡事件

*/