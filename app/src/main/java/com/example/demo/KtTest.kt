package com.example.demo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun main()  {
    KtFragment().start()
}

class KtFragment {
    // 匿名内部类测试
    fun start() {

        // 启动协程demo
        startCoroutineDemo()

        // 阻塞主线程，等待协程执行完成
        Thread.sleep(15000)
    }

    /**
     * 协程执行原理演示
     */
    private fun startCoroutineDemo() {
        println("=== 协程Demo开始 ===")

        // 创建协程作用域 (使用 Dispatchers.Default 替代 Dispatchers.Main)
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        scope.launch {
            println("主协程开始执行")

            try {
                // 调用挂起函数
                val result = messengerAsync()
                println("协程恢复后收到结果: $result")

                // 演示并发执行
                demonstrateConcurrency()

                // 演示挂起机制
                demonstrateSuspendMechanism()

            } catch (e: Exception) {
                println("协程执行出错: ${e.message}")
            }
        }
    }

    /**
     * 模拟异步操作的挂起函数
     */
    private suspend fun messengerAsync(): String {
        println("1. messengerAsync函数开始执行")

        // 🔧 在这里挂起，后面的代码不会立即执行
        val ret = suspendCoroutine<String> { continuation ->
            println("2. 准备挂起，当前线程: ${Thread.currentThread().name}")

            // 模拟异步调用（比如网络请求）
            asyncInvoke { result ->
                println("异步回调执行，线程: ${Thread.currentThread().name}")
                continuation.resume(result)
            }

            println("3. 注册回调完成，即将挂起")
        }

        // 🚨 这些代码在挂起期间不会执行！只有恢复后才执行
        println("4. 恢复后才执行这里，线程: ${Thread.currentThread().name}")
        println("5. 收到异步结果: $ret")

        return ret
    }

    /**
     * 模拟异步调用
     */
    private fun asyncInvoke(callback: (String) -> Unit) {
        println("异步操作开始，模拟网络请求...")

        // 使用线程池模拟异步操作
        Thread {
            Thread.sleep(2000) // 模拟2秒的网络延迟
            println("异步操作完成，准备回调")
            callback("异步操作成功的结果")
        }.start()
    }

    /**
     * 演示协程并发执行
     */
    private suspend fun demonstrateConcurrency() {
        println("=== 开始演示并发执行 ===")

        val startTime = System.currentTimeMillis()

        // 使用 coroutineScope 创建协程作用域来执行并发操作
        coroutineScope {
            // 并发执行多个挂起函数
            val deferred1 = async { networkCall("API-1", 1000) }
            val deferred2 = async { networkCall("API-2", 1500) }
            val deferred3 = async { networkCall("API-3", 800) }

            // 等待所有结果
            val result1 = deferred1.await()
            val result2 = deferred2.await()
            val result3 = deferred3.await()

            val endTime = System.currentTimeMillis()

            println("并发结果: $result1, $result2, $result3")
            println("总耗时: ${endTime - startTime}ms (如果串行执行需要3300ms)")
        }

        // 对比串行执行
        demonstrateSequential()
    }

    /**
     * 演示串行执行对比
     */
    private suspend fun demonstrateSequential() {
        println("=== 开始演示串行执行 ===")

        val startTime = System.currentTimeMillis()

        // 串行执行
        val result1 = networkCall("Sequential-API-1", 500)
        val result2 = networkCall("Sequential-API-2", 500)
        val result3 = networkCall("Sequential-API-3", 500)

        val endTime = System.currentTimeMillis()

        println("串行结果: $result1, $result2, $result3")
        println("串行总耗时: ${endTime - startTime}ms")
    }

    /**
     * 模拟网络调用
     */
    private suspend fun networkCall(apiName: String, delayMs: Long): String {
        println("$apiName 开始调用，线程: ${Thread.currentThread().name}")

        return withContext(Dispatchers.IO) {
            delay(delayMs)
            println("$apiName 调用完成")
            "$apiName 的响应数据"
        }
    }

    /**
     * 演示 suspend 函数的挂起机制
     * 重点：只有调用 suspend 函数的协程会被挂起，作用域不会被挂起
     */
    private suspend fun demonstrateSuspendMechanism() {
        println("\n=== 演示 suspend 函数挂起机制 ===")

        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        // 启动协程A - 会被挂起
        scope.launch {
            println("🔴 协程A开始执行，线程: ${Thread.currentThread().name}")

            println("🔴 协程A即将调用suspend函数")
            val result = longRunningTask("协程A的任务", 3000)

            println("🔴 协程A恢复执行，结果: $result")
            println("🔴 协程A完成")
        }

        // 启动协程B - 不会被挂起，继续执行
        scope.launch {
            println("🟢 协程B开始执行，线程: ${Thread.currentThread().name}")

            repeat(5) { i ->
                delay(800) // 这也是suspend函数，但只挂起协程B
                println("🟢 协程B继续工作中... ${i + 1}/5")
            }

            println("🟢 协程B完成")
        }

        // 启动协程C - 也不会被挂起
        scope.launch {
            println("🔵 协程C开始执行，线程: ${Thread.currentThread().name}")

            repeat(3) { i ->
                delay(1200)
                println("🔵 协程C也在工作... ${i + 1}/3")
            }

            println("🔵 协程C完成")
        }

        // 等待所有协程完成
        delay(5000)
        println("=== 挂起机制演示完成 ===\n")
    }

    /**
     * 模拟长时间运行的任务
     */
    private suspend fun longRunningTask(taskName: String, delayMs: Long): String {
        println("⏳ $taskName 开始执行，需要 ${delayMs}ms")

        return withContext(Dispatchers.IO) {
            delay(delayMs)
            "$taskName 执行完成"
        }
    }
}
