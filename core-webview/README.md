# Core-WebView 模块

## 概述

`core-webview` 是一个独立的WebView核心模块，提供了完整的WebView功能，支持组件化架构和TheRouter路由跳转。

## 功能特性

- ✅ 完整的WebView功能实现
- ✅ 支持TheRouter路由注解
- ✅ 自定义标题栏和进度条
- ✅ JavaScript支持
- ✅ SSL错误处理
- ✅ 后退导航支持
- ✅ DataBinding集成
- ✅ 生命周期管理

## 架构设计

### 核心组件

- **WebViewActivity**: 主要的WebView页面，支持路由跳转
- **布局文件**: 使用DataBinding的响应式布局
- **资源文件**: 完整的图标、颜色和样式定义

### 技术栈

- Kotlin
- TheRouter (路由框架)
- DataBinding
- WebView API
- Material Design

## 使用方法

### 1. 直接启动

```kotlin
WebViewActivity.start(
    context = this,
    title = "页面标题",
    url = "https://example.com"
)
```

### 2. TheRouter路由跳转

```kotlin
TheRouter.build("/webview")
    .withString("title", "页面标题")
    .withString("url", "https://example.com")
    .navigation()
```

### 3. 在其他模块中使用

在需要使用WebView的模块的 `build.gradle` 中添加依赖：

```gradle
dependencies {
    implementation project(":core-webview")
}
```

## 项目集成

### 已完成的集成

1. **feature-home模块**: 文章列表点击跳转到WebView
2. **app模块**: 主模块依赖配置
3. **settings.gradle**: 模块配置

### HomeFragment集成示例

```kotlin
// 在文章点击事件中
homeAdapter = HomeAdapter { article ->
    // 跳转到WebView页面
    com.example.core.webview.WebViewActivity.start(
        requireContext(),
        article.title,
        article.link
    )
}
```

## 配置说明

### AndroidManifest.xml权限

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### ProGuard配置

已包含完整的混淆规则，支持：
- WebView JavaScript接口
- TheRouter注解保留
- 数据绑定相关规则

## 安全特性

1. **SSL错误处理**: 可配置的SSL错误策略
2. **URL过滤**: 支持自定义URL拦截规则
3. **JavaScript控制**: 可配置的JavaScript执行权限
4. **网络状态检测**: Internet连接状态监控

## 性能优化

1. **硬件加速**: 启用WebView硬件加速
2. **缓存策略**: 智能缓存管理
3. **内存管理**: 生命周期感知的内存清理
4. **进度反馈**: 实时加载进度显示

## 扩展性

模块设计遵循开闭原则，支持：
- 自定义WebViewClient
- 自定义WebChromeClient
- JavaScript接口扩展
- 主题样式定制

## 依赖关系

```
core-webview
├── core-common (基础组件)
├── TheRouter (路由框架)
└── AndroidX (系统组件)
```

## 更新日志

### v1.0.0
- 初始版本发布
- 完整WebView功能实现
- TheRouter路由支持
- 与feature-home模块集成

## 注意事项

1. 确保网络权限已正确配置
2. 生产环境需要更严格的SSL验证
3. JavaScript接口需要添加@JavascriptInterface注解
4. 大文件下载建议使用DownloadManager

## 技术支持

遇到问题时，请检查：
1. 模块依赖是否正确配置
2. 权限是否已声明
3. 路由注解是否正确
4. 网络连接是否正常

