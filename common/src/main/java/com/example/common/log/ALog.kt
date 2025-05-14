package com.example.common.log

import android.annotation.SuppressLint
import android.util.Log

@SuppressLint("NewApi")
class ALog {
    companion object {
        private const val MAX_LOG_LENGTH = 4000
        private const val LOG_TAG = "ALog"
        private val logLock = Any()

        /**
         * 获取调用日志方法的代码位置信息
         * @return 符合标准格式的调用位置信息
         */
        private fun getCallerInfo(): String {
            val stackTrace = Thread.currentThread().stackTrace
            var aLogIndex = -1
            // 找到 ALog 类的调用位置
            for (i in stackTrace.indices) {
                val element = stackTrace[i]
                if (element.className.startsWith(ALog::class.java.name)) {
                    aLogIndex = i
                    break
                }
            }

            // 从 ALog 调用的下一个位置开始查找真正的调用点
            if (aLogIndex != -1) {
                for (i in aLogIndex + 1 until stackTrace.size) {
                    val element = stackTrace[i]
                    if (!element.className.startsWith(ALog::class.java.name)) {
                        val fileName = element.fileName ?: "UnknownFile.kt"
                        return "${element.className}($fileName:${element.lineNumber})"
                    }
                }
            }
            return "Unknown(UnknownFile.kt:0)"
        }

        /**
         * 统一的日志打印方法
         * @param priority 日志级别
         * @param userTag 用户输入的标签，可为空
         * @param message 日志内容
         * @param throwable 异常对象，可为 null
         */
        private fun log(priority: Int, userTag: String? = null, message: String, throwable: Throwable? = null) {
            val thread = Thread.currentThread()
            val callerInfo = getCallerInfo()
            val tagInfo = buildString {
                if (!userTag.isNullOrEmpty()) {
                    append(userTag).append(" - ")
                }
                append("    --- ${thread.name}[${thread.id}] $callerInfo")
            }

            synchronized(logLock) {
                // 打印标签信息
                Log.println(priority, LOG_TAG, tagInfo)

                // 分段打印长消息
                var i = 0
                val length = message.length
                while (i < length) {
                    var newline = message.indexOf('\n', i)
                    newline = if (newline != -1) newline else length
                    do {
                        val end = Math.min(newline, i + MAX_LOG_LENGTH)
                        val part = message.substring(i, end)
                        Log.println(priority, LOG_TAG, part)
                        i = end
                    } while (i < newline)
                    i++
                }

                // 打印异常堆栈信息
                if (throwable != null) {
                    Log.println(priority, LOG_TAG, Log.getStackTraceString(throwable))
                }
            }
        }

        /**
         * 打印 VERBOSE 级别的日志
         * @param message 日志内容
         */
        @JvmStatic
        fun v(message: String = "") {
            log(Log.VERBOSE, null, message, null)
        }

        /**
         * 打印 VERBOSE 级别的日志
         * @param tag 用户输入的标签，可为空
         * @param message 日志内容
         */
        @JvmStatic
        fun v(tag: String? = null, message: String = "") {
            log(Log.VERBOSE, tag, message, null)
        }

        /**
         * 打印 VERBOSE 级别的日志
         * @param tag 用户输入的标签，可为空
         * @param message 日志内容
         * @param throwable 异常对象，可为 null
         */
        @JvmStatic
        fun v(tag: String? = null, message: String = "", throwable: Throwable? = null) {
            log(Log.VERBOSE, tag, message, throwable)
        }


        /**
         * 打印 DEBUG 级别的日志
         * @param message 日志内容
         */
        @JvmStatic
        fun d(message: String = "") {
            log(Log.DEBUG, null, message, null)
        }

        /**
         * 打印 DEBUG 级别的日志
         * @param tag 用户输入的标签，可为空
         * @param message 日志内容
         */
        @JvmStatic
        fun d(tag: String? = null, message: String = "") {
            log(Log.DEBUG, tag, message, null)
        }

        /**
         * 打印 DEBUG 级别的日志
         * @param tag 用户输入的标签，可为空
         * @param message 日志内容
         * @param throwable 异常对象，可为 null
         */
        @JvmStatic
        fun d(tag: String? = null, message: String = "", throwable: Throwable? = null) {
            log(Log.DEBUG, tag, message, throwable)
        }

        /**
         * 打印 INFO 级别的日志
         * @param message 日志内容
         */
        @JvmStatic
        fun i(message: String = "") {
            log(Log.INFO, null, message, null)
        }

        /**
         * 打印 INFO 级别的日志
         * @param tag 用户输入的标签，可为空
         * @param message 日志内容
         */
        @JvmStatic
        fun i(tag: String? = null, message: String = "") {
            log(Log.INFO, tag, message, null)
        }

        /**
         * 打印 INFO 级别的日志
         * @param tag 用户输入的标签，可为空
         * @param message 日志内容
         * @param throwable 异常对象，可为 null
         */
        @JvmStatic
        fun i(tag: String? = null, message: String = "", throwable: Throwable? = null) {
            log(Log.INFO, tag, message, throwable)
        }

        /**
         * 打印 WARN 级别的日志
         * @param message 日志内容
         */
        @JvmStatic
        fun w(message: String = "") {
            log(Log.WARN, null, message, null)
        }

        /**
         * 打印 WARN 级别的日志
         * @param tag 用户输入的标签，可为空
         * @param message 日志内容
         */
        @JvmStatic
        fun w(tag: String? = null, message: String = "") {
            log(Log.WARN, tag, message, null)
        }

        /**
         * 打印 WARN 级别的日志
         * @param tag 用户输入的标签，可为空
         * @param message 日志内容
         * @param throwable 异常对象，可为 null
         */
        @JvmStatic
        fun w(tag: String? = null, message: String = "", throwable: Throwable? = null) {
            log(Log.WARN, tag, message, throwable)
        }

        /**
         * 打印 ERROR 级别的日志
         * @param message 日志内容
         */
        @JvmStatic
        fun e(message: String = "") {
            log(Log.ERROR, null, message, null)
        }

        /**
         * 打印 ERROR 级别的日志
         * @param tag 用户输入的标签，可为空
         * @param message 日志内容
         */
        @JvmStatic
        fun e(tag: String? = null, message: String = "") {
            log(Log.ERROR, tag, message, null)
        }

        /**
         * 打印 ERROR 级别的日志
         * @param tag 用户输入的标签，可为空
         * @param message 日志内容
         * @param throwable 异常对象，可为 null
         */
        @JvmStatic
        fun e(tag: String? = null, message: String = "", throwable: Throwable? = null) {
            log(Log.ERROR, tag, message, throwable)
        }

        /**
         * 打印完整调用栈信息
         */
        @JvmStatic
        fun trace(message: String = "") {
            val stackTrace = Thread.currentThread().stackTrace
            var firstTraceFrameIndex = 1
            for (i in stackTrace.indices) {
                val element = stackTrace[i]
                if (element.className.startsWith(ALog::class.java.name) &&
                    (element.methodName.contains("trace"))) {
                    firstTraceFrameIndex = i
                    break
                }
            }

            val stackTraceString = buildString {
                for (index in stackTrace.indices.drop(firstTraceFrameIndex + 1)) {
                    val element = stackTrace[index]
                    append("at ").append(element.className).append(".").append(element.methodName)
                        .append("(").append(element.fileName).append(":").append(element.lineNumber).append(")\n")
                }
            }
            log(Log.ERROR, null, message = message + "\n" + stackTraceString)
        }
    }
}
