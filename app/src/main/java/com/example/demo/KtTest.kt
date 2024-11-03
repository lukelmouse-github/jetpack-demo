package com.example.demo

fun main()  {
    KtFragment().start()
}

class KtFragment {
    private val outerField = "Outer Field"
    // 匿名内部类测试
    fun start() {
        var test = "test Field"

        test = "123";
        // 匿名内部类
        val runnable: Runnable = object : Runnable {
            private val innerField = "Inner Field"
            override fun run() {
                // 访问外部类的成员
                println(outerField)
                // 访问匿名内部类的成员
                println(innerField)
                println(test)
                test = "456";
                /**
                 * test2实际上在kotlin中被包了一层RefObject
                 * 这样子引用不会变，所以改引用对象里面的值没问题。
                 */
                println(test)
            }
        }


        // 运行匿名内部类
        runnable.run()
    }
}