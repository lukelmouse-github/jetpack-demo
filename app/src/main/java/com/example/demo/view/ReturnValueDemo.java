package com.example.demo.view;

/**
 * 演示dispatchTouchEvent返回值的含义和作用
 */
public class ReturnValueDemo {

    /*
    ========== 事件分发的层次结构 ==========
    
    Activity (最顶层)
        ↓ 调用
    AViewGroup (父ViewGroup)
        ↓ 调用  
    BViewGroup (子ViewGroup)
        ↓ 调用
    CView (最底层子View)
    
    ========== 返回值的传递路径 ==========
    
    CView → BViewGroup → AViewGroup → Activity
     ↑返回值   ↑返回值      ↑返回值      ↑返回值
    
    */

    // ========== 示例1：事件被子View处理 ==========
    public void example1_EventHandled() {
        /*
        // CView处理了事件
        CView.onTouchEvent() → return true; // "我处理了"
        
        // BViewGroup收到子View的返回值
        BViewGroup.dispatchTouchEvent() {
            boolean handled = cView.dispatchTouchEvent(event);
            if (handled) { // handled = true
                return true; // 向父级汇报："事件已处理"
            }
        }
        
        // AViewGroup收到子View的返回值  
        AViewGroup.dispatchTouchEvent() {
            boolean handled = bViewGroup.dispatchTouchEvent(event);
            if (handled) { // handled = true
                return true; // 向Activity汇报："事件已处理"
            }
        }
        
        // Activity收到最终结果
        Activity收到: true → "好的，事件处理完了，我不用管了"
        */
    }

    // ========== 示例2：事件没有被处理 ==========
    public void example2_EventNotHandled() {
        /*
        // CView没有处理事件
        CView.onTouchEvent() → return false; // "我不处理"
        
        // BViewGroup收到子View的返回值
        BViewGroup.dispatchTouchEvent() {
            boolean handled = cView.dispatchTouchEvent(event);
            if (handled) { // handled = false
                return true;
            }
            // 子View没处理，自己试试
            return onTouchEvent(event); // 假设也返回false
        }
        
        // AViewGroup收到子ViewGroup的返回值
        AViewGroup.dispatchTouchEvent() {
            boolean handled = bViewGroup.dispatchTouchEvent(event);
            if (handled) { // handled = false
                return true; 
            }
            // 子ViewGroup没处理，自己试试
            return onTouchEvent(event); // 假设也返回false
        }
        
        // Activity收到最终结果
        Activity收到: false → "没人处理这个事件，那就算了"
        */
    }

    // ========== 示例3：父ViewGroup拦截事件 ==========
    public void example3_ParentIntercept() {
        /*
        // AViewGroup决定拦截事件
        AViewGroup.dispatchTouchEvent() {
            if (onInterceptTouchEvent(event)) { // 返回true，要拦截
                return onTouchEvent(event); // 自己处理，假设返回true
            }
        }
        
        // Activity收到结果
        Activity收到: true → "AViewGroup处理了事件"
        
        // 注意：BViewGroup和CView根本不会收到事件！
        */
    }

    /*
    ========== 关键理解点 ==========
    
    1. 返回值是给【调用者】看的
       - 子View的返回值 → 父ViewGroup看
       - 父ViewGroup的返回值 → 更上级的ViewGroup或Activity看
    
    2. 返回值决定事件是否继续传递
       - true: "事件已解决，不用再传递了"
       - false: "事件没解决，请继续处理"
    
    3. 就像一个责任链
       - 每个环节都可以选择处理或者不处理
       - 处理了就返回true，没处理就返回false
       - 最终结果会一层层向上汇报
    
    ========== 类比理解 ==========
    
    想象一个公司的问题处理流程：
    
    员工遇到问题 → 组长 → 经理 → 总监 → 老板
    
    如果组长解决了：
    组长告诉经理："问题解决了"(return true)
    经理告诉总监："下面解决了"(return true)  
    总监告诉老板："都处理好了"(return true)
    
    如果都解决不了：
    每个人都说："我处理不了"(return false)
    最终老板知道："这个问题没人能解决"
    */
}