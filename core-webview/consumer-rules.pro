# WebView模块的consumer规则
# 保留TheRouter注解的类
-keep class com.example.core.webview.WebViewActivity { *; }

# 保留WebView相关的JavaScript接口
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

