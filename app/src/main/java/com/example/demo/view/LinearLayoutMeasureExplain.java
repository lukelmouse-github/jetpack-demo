package com.example.demo.view;

/**
 * LinearLayout测量次数详解
 * 解释为什么测量次数不是固定的两次
 */
public class LinearLayoutMeasureExplain {

    /*
    ===============================================================
    🎯 LinearLayout测量次数：并非固定两次！
    ===============================================================
    
    测量次数取决于多个因素：
    • 是否有layout_weight
    • LinearLayout自身的尺寸策略
    • 是否开启baselineAligned
    • 子View的复杂度
    
    */

    /*
    ===============================================================
    📊 测量次数分析表
    ===============================================================
    
    场景                           | 测量次数 | 原因
    -----------------------------|----------|------------------
    所有子View固定尺寸              | 1次      | 直接计算即可
    有weight，无wrap_content       | 2次      | 经典两阶段测量
    有weight + wrap_content        | 2-3次    | 需要重新计算总尺寸
    复杂嵌套 + baseline对齐         | 3+次     | 多轮迭代调整
    动态添加/移除子View             | 1+次     | 按需重新测量
    
    */

    /*
    ===============================================================
    🔄 情况1：一次测量（最简单）
    ===============================================================
    
    XML示例：
    <LinearLayout android:layout_width="match_parent"
                  android:layout_height="wrap_content">
        <View android:layout_width="100dp" 
              android:layout_height="50dp" />
        <View android:layout_width="200dp" 
              android:layout_height="60dp" />
    </LinearLayout>
    
    测量过程：
    第1次测量：
    ├─ 测量子View1: 100dp x 50dp
    ├─ 测量子View2: 200dp x 60dp  
    ├─ 计算LinearLayout尺寸: 300dp x 60dp
    └─ 完成 ✅
    
    为什么只需要1次？
    • 所有子View尺寸确定，无需重新分配空间
    • LinearLayout可以直接累加计算自身尺寸
    
    */

    /*
    ===============================================================
    🔄🔄 情况2：两次测量（经典场景）
    ===============================================================
    
    XML示例：
    <LinearLayout android:layout_width="300dp"
                  android:layout_height="wrap_content">
        <View android:layout_width="100dp" 
              android:layout_height="50dp" />
        <View android:layout_width="0dp"
              android:layout_weight="1"
              android:layout_height="60dp" />
        <View android:layout_width="0dp"
              android:layout_weight="2"  
              android:layout_height="40dp" />
    </LinearLayout>
    
    测量过程：
    第1次测量（收集信息）：
    ├─ 测量固定尺寸子View：100dp
    ├─ 跳过weight子View（暂时按0dp处理）
    ├─ 计算剩余空间：300dp - 100dp = 200dp
    └─ 按weight比例分配：weight1=67dp, weight2=133dp
    
    第2次测量（重新测量weight子View）：
    ├─ 重新测量weight子View1：67dp x 60dp
    ├─ 重新测量weight子View2：133dp x 40dp
    ├─ 计算LinearLayout最终尺寸：300dp x 60dp
    └─ 完成 ✅
    
    为什么需要2次？
    • 第1次：确定有多少剩余空间可以分配
    • 第2次：按weight比例重新测量相关子View
    
    */

    /*
    ===============================================================
    🔄🔄🔄 情况3：多次测量（复杂场景）
    ===============================================================
    
    XML示例：
    <LinearLayout android:layout_width="wrap_content"
                  android:layout_height="wrap_content"
                  android:baselineAligned="true">
        <TextView android:layout_width="0dp"
                  android:layout_weight="1"
                  android:text="Short" />
        <TextView android:layout_width="0dp"
                  android:layout_weight="1"
                  android:text="Very Long Text That Wraps" />
    </LinearLayout>
    
    测量过程：
    第1次测量（初步测量）：
    ├─ LinearLayout是wrap_content，先给子View一个初始约束
    ├─ 测量TextView1: 需要50dp宽度
    ├─ 测量TextView2: 需要150dp宽度  
    └─ 初步估算LinearLayout需要200dp宽度
    
    第2次测量（重新分配）：
    ├─ 按weight=1:1重新分配200dp
    ├─ 重新测量TextView1: 100dp宽度
    ├─ 重新测量TextView2: 100dp宽度（可能需要换行）
    └─ 发现TextView2换行后高度变化
    
    第3次测量（baseline对齐调整）：
    ├─ 根据baseline对齐要求调整
    ├─ 可能需要重新调整某些TextView的位置
    └─ 最终确定LinearLayout尺寸
    
    为什么需要3次？
    • wrap_content需要根据子View结果调整
    • weight分配影响子View尺寸
    • baseline对齐需要额外调整
    
    */

    /*
    =============================================================== 
    🎯 LinearLayout.onMeasure()源码关键逻辑
    ===============================================================
    
    // LinearLayout.java 的onMeasure方法
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }
    
    // measureVertical的简化逻辑
    void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        
        // 🔄 第一轮：测量所有子View
        for (int i = 0; i < count; ++i) {
            final View child = getVirtualChildAt(i);
            
            if (lp.weight > 0) {
                // 有weight的先记录，后面处理
                totalWeight += lp.weight;
                skippedMeasure = true;
            } else {
                // 🔄 无weight的直接测量
                measureChildBeforeLayout(child, i, widthMeasureSpec, ...);
                totalHeight += child.getMeasuredHeight();
            }
        }
        
        // 🔄 第二轮：处理weight子View (如果有的话)
        if (totalWeight > 0) {
            for (int i = 0; i < count; ++i) {
                final View child = getVirtualChildAt(i);
                if (lp.weight > 0) {
                    // 🔄 重新测量weight子View
                    int share = (int) (lp.weight * delta / totalWeight);
                    measureChildBeforeLayout(child, i, widthMeasureSpec, ...);
                }
            }
        }
        
        // 🔄 可能的第三轮：特殊情况调整
        if (shouldRemeasure || baselineAligned) {
            // 某些情况下需要额外的测量轮次
        }
    }
    
    */

    /*
    ===============================================================
    🎯 实际开发中的优化建议
    ===============================================================
    
    1. 🚀 减少测量次数的方法：
       • 避免不必要的layout_weight
       • 尽量使用固定尺寸而不是wrap_content
       • 减少深层嵌套的LinearLayout
    
    2. 🎯 性能友好的写法：
       // ✅ 好：固定尺寸，只需1次测量
       <LinearLayout>
           <View android:layout_width="100dp" android:layout_height="50dp" />
           <View android:layout_width="200dp" android:layout_height="50dp" />
       </LinearLayout>
       
       // ⚠️ 慎用：可能需要3次测量
       <LinearLayout android:layout_width="wrap_content"
                     android:baselineAligned="true">
           <TextView android:layout_weight="1" />
           <TextView android:layout_weight="1" />
       </LinearLayout>
    
    3. 🔧 现代替代方案：
       • 使用ConstraintLayout替代复杂的LinearLayout嵌套
       • 使用FlexboxLayout处理复杂的权重布局
       • 使用RecyclerView处理动态列表
    
    */

    /*
    ===============================================================
    📋 总结
    ===============================================================
    
    LinearLayout的测量次数：
    
    • 📏 1次测量：所有子View固定尺寸
    • 🔄 2次测量：有weight但布局简单  
    • 🔄🔄 3+次测量：复杂的weight + wrap_content + baseline
    
    关键因素：
    • layout_weight的使用
    • LinearLayout自身的尺寸策略
    • baselineAligned设置
    • 子View的复杂程度
    
    性能考虑：
    • 测量是昂贵操作，应该尽量减少
    • 复杂布局考虑使用ConstraintLayout
    • 避免深层嵌套的LinearLayout
    
    */
}