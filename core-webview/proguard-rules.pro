# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# WebView相关混淆规则
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# 保留WebView JavaScript接口
-keepattributes JavascriptInterface
-keepattributes *Annotation*

# TheRouter相关
-keep class com.therouter.** { *; }
-keep class * implements com.therouter.router.route.IRouteRoot { *; }
-keep class * implements com.therouter.router.route.IInterceptor { *; }

