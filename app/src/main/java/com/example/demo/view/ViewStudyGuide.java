/*
 * View学习Demo使用指南
 * 
 * 这个demo包含多个自定义View，展示了Android View系统的核心概念：
 * 
 * === 1. CustomDemoView - 基础的测量布局绘制演示 ===
 * 
 * 学习要点：
 * - onMeasure(): 理解MeasureSpec的三种模式 EXACTLY/AT_MOST/UNSPECIFIED
 * - onLayout(): 观察View如何确定最终位置和尺寸
 * - onDraw(): 了解Canvas绘制过程
 * - onTouchEvent(): 触摸事件处理和坐标转换
 * 
 * 观察方法：
 * 1. 启动app后观察初始化日志
 * 2. 点击"重置计数器"按钮观察requestLayout()触发的重新测量
 * 3. 触摸CustomView区域观察触摸事件处理
 * 4. 注意测量、布局、绘制的调用次数和顺序
 * 
 * === 2. TouchEventDemoViewGroup - 触摸事件分发演示 ===
 * 
 * 学习要点：
 * - dispatchTouchEvent(): 事件分发的入口
 * - onInterceptTouchEvent(): ViewGroup如何拦截事件
 * - onTouchEvent(): 事件的最终处理
 * - 事件传递的流程：dispatch -> intercept -> 子View dispatch -> onTouchEvent
 * 
 * 观察方法：
 * 1. 点击黄色子View区域，观察完整的事件分发流程
 * 2. 点击"切换事件拦截"按钮改变拦截状态
 * 3. 对比拦截开启和关闭时的事件流程差异
 * 4. 注意事件的返回值如何影响后续事件传递
 * 
 * === 3. ViewLifecycleDemo - View生命周期演示 ===
 * 
 * 学习要点：
 * - onAttachedToWindow/onDetachedFromWindow: View添加到窗口/从窗口移除
 * - onVisibilityChanged: 可见性变化回调
 * - onSizeChanged: 尺寸变化回调
 * - onFocusChanged: 焦点变化回调
 * 
 * 观察方法：
 * 1. 切换Fragment观察attach/detach回调
 * 2. 点击"切换可见性"按钮观察可见性回调
 * 3. 旋转屏幕观察尺寸变化回调
 * 
 * === 4. AdvancedMeasureDemo - 高级测量演示 ===
 * 
 * 学习要点：
 * - ViewGroup如何测量子View
 * - MeasureSpec的创建和传递
 * - 不同类型子View的测量策略
 * - 父View如何根据子View计算自身尺寸
 * 
 * 观察方法：
 * 1. 观察不同子View的测量过程
 * 2. 点击"触发重新测量"观察测量轮数
 * 3. 理解固定尺寸、包裹内容、填充父View的区别
 * 
 * === 5. NestedTouchEventDemo - 多层嵌套触摸事件演示 ===
 * 
 * 学习要点：
 * - 多层ViewGroup的事件分发链
 * - 事件拦截对事件链的影响
 * - ACTION_CANCEL事件的产生
 * - 复杂嵌套结构中的事件处理
 * 
 * 观察方法：
 * 1. 点击最内层的青色区域
 * 2. 观察事件如何从外层传递到内层
 * 3. 注意第2层拦截事件时的ACTION_CANCEL
 * 
 * === 调试技巧 ===
 * 
 * 1. 日志过滤：
 *    - adb logcat | grep "CustomDemoView\|TouchEvent\|ViewLifecycle\|AdvancedMeasure\|NestedTouch"
 * 
 * 2. 关键观察点：
 *    - MeasureSpec的mode和size
 *    - 事件的action类型和坐标
 *    - 方法的返回值和调用顺序
 *    - 子View和父View的尺寸关系
 * 
 * 3. 实验方法：
 *    - 修改onMeasure的返回值观察布局变化
 *    - 修改onTouchEvent的返回值观察事件传递
 *    - 修改onInterceptTouchEvent的返回值观察拦截效果
 * 
 * === 常见问题解答 ===
 * 
 * Q: 为什么onMeasure会被调用多次？
 * A: ViewGroup可能需要多次测量来确定最佳布局，特别是在复杂布局中。
 * 
 * Q: 为什么有时候onDraw不被调用？
 * A: View可能被父View遮挡，或者View本身不可见。
 * 
 * Q: 如何理解事件消费和拦截的区别？
 * A: 拦截是阻止事件继续向下传递，消费是处理事件并停止向上传递。
 * 
 * Q: MeasureSpec的三种模式什么时候会用到？
 * A: EXACTLY-精确尺寸/match_parent，AT_MOST-最大限制/wrap_content，UNSPECIFIED-无限制/ScrollView内部
 */

package com.example.demo.view;

public class ViewStudyGuide {
    // 这个类仅包含学习指南注释，不需要实际代码
    /**
     * 完整的View学习Demo系统
     * 📚 包含的学习模块：
     * CustomDemoView - 基础View演示
     * ✅ 详细的onMeasure流程，包含MeasureSpec解析
     * ✅ onLayout布局过程日志
     * ✅ onDraw绘制过程，包含触摸交互
     * ✅ onTouchEvent触摸事件处理
     * ✅ 实时显示调用次数计数器
     * TouchEventDemoViewGroup - 基础事件分发演示
     * ✅ dispatchTouchEvent事件分发日志
     * ✅ onInterceptTouchEvent事件拦截控制
     * ✅ 包含子View的完整事件链演示
     * ✅ 可以动态切换拦截模式
     * ViewLifecycleDemo - View生命周期演示
     * ✅ 完整的生命周期回调监听
     * ✅ 可见性变化、窗口变化、尺寸变化监听
     * ✅ 实用的生命周期学习工具
     * AdvancedMeasureDemo - 高级测量演示
     * ✅ ViewGroup如何测量多个不同类型的子View
     * ✅ MeasureSpec的创建和传递过程
     * ✅ 固定尺寸、包裹内容、填充父View的区别演示
     * ✅ 多轮测量过程的详细日志
     * NestedTouchEventDemo - 多层嵌套事件分发演示
     * ✅ 3层ViewGroup嵌套的复杂事件分发
     * ✅ 中间层事件拦截演示
     * ✅ ACTION_CANCEL事件的产生演示
     * ✅ 完整的事件传递链路追踪
     * ViewStudyGuide - 详细学习指南
     * ✅ 每个模块的学习要点
     * ✅ 具体的观察方法和操作步骤
     * ✅ 调试技巧和日志过滤方法
     * ✅ 常见问题解答
     */
}