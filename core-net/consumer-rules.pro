# Consumer ProGuard Rules for core-net module

# Retrofit 相关
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp 相关
-dontwarn okhttp3.**
-dontwarn okio.**

# Moshi 相关
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-dontwarn org.jetbrains.annotations.**
-keep class kotlin.Metadata { *; }

# 保留 API 数据模型类
-keep class com.example.core.net.model.** { *; }

# 保留网络异常类
-keep class com.example.core.net.exception.** { *; }

