# Consumer ProGuard Rules for core-common module

# TheRouter 相关
-keep class androidx.annotation.Keep
-keep @androidx.annotation.Keep class * {*;}
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}
-keepclasseswithmembers class * {
    @com.therouter.router.Autowired <fields>;
}

# Moshi 相关
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-dontwarn org.jetbrains.annotations.**
-keep class kotlin.Metadata { *; }

# Retrofit 相关
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp 相关
-dontwarn okhttp3.**
-dontwarn okio.**

# Glide 相关
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# MMKV 相关
-keep class com.tencent.mmkv.** { *; }
-dontwarn com.tencent.mmkv.**

# Koin 相关
-keep class org.koin.** { *; }
-keep interface org.koin.** { *; }

