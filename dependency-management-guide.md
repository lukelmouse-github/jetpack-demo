# 依赖管理使用指南

本项目采用统一版本管理策略，所有第三方依赖的版本号都在 `versions.gradle` 文件中统一定义和管理。

## 📁 文件结构

```
jetpack-demo/
├── versions.gradle          # 统一版本管理文件
├── build.gradle            # 根项目配置，引入 versions.gradle
├── app/
│   └── build.gradle        # app 模块配置
└── core-common/
    └── build.gradle        # core-common 模块配置
```

## 🎯 核心优势

✅ **版本统一管理**：所有依赖版本在一个文件中维护
✅ **避免版本冲突**：确保所有模块使用相同的依赖版本
✅ **升级简化**：只需在一个地方修改版本号
✅ **维护性提升**：清晰的版本分类和命名规范

## 📝 使用方法

### 1. 查看版本定义

所有版本变量都定义在 `versions.gradle` 文件中，按类别分组：

```gradle
// ========== AndroidX 基础库版本 ==========
androidx_core_version = '1.10.1'
androidx_appcompat_version = '1.6.1'
material_version = '1.9.0'

// ========== 网络相关版本 ==========
retrofit_version = '2.9.0'
okhttp_version = '4.11.0'
moshi_version = '1.15.0'
```

### 2. 在模块中使用版本变量

在各个模块的 `build.gradle` 文件中使用变量：

```gradle
dependencies {
    // ❌ 错误写法 - 硬编码版本号
    implementation 'androidx.core:core-ktx:1.10.1'

    // ✅ 正确写法 - 使用版本变量
    implementation "androidx.core:core-ktx:$androidx_core_version"
}
```

### 3. Android 配置使用版本变量

```gradle
android {
    compileSdk compile_sdk_version

    defaultConfig {
        minSdk min_sdk_version
        targetSdk target_sdk_version
        versionCode version_code
        versionName version_name
    }
}
```

## 🔧 添加新依赖

### 步骤 1：在 versions.gradle 中定义版本

```gradle
ext {
    // 在对应分类中添加新的版本变量
    new_library_version = '1.0.0'
}
```

### 步骤 2：在模块中使用

```gradle
dependencies {
    implementation "com.example:new-library:$new_library_version"
}
```

## 📊 版本分类说明

| 分类 | 说明 | 示例变量 |
|------|------|----------|
| Android 相关 | SDK 版本、应用版本等 | `compile_sdk_version`, `min_sdk_version` |
| Kotlin 相关 | Kotlin 和协程版本 | `kotlin_version`, `coroutines_version` |
| AndroidX 基础库 | 核心 AndroidX 组件 | `androidx_core_version`, `material_version` |
| Jetpack 组件 | Lifecycle、Navigation 等 | `lifecycle_version`, `navigation_version` |
| 网络相关 | Retrofit、OkHttp 等 | `retrofit_version`, `okhttp_version` |
| 图片加载 | Glide、Coil 等 | `glide_version`, `coil_version` |
| 依赖注入 | Koin、Hilt 等 | `koin_version`, `hilt_version` |
| 测试相关 | JUnit、Espresso 等 | `junit_version`, `espresso_version` |

## 🚀 版本升级流程

### 单个库升级

1. 在 `versions.gradle` 中修改对应版本号
2. 同步项目，所有使用该库的模块都会自动使用新版本

### 批量升级

1. 在 `versions.gradle` 中批量修改版本号
2. 检查 CHANGELOG 确认兼容性
3. 运行测试确保升级成功

## ⚠️ 注意事项

### kapt 依赖特殊处理

注解处理器（kapt）依赖需要在使用的模块中重新声明：

```gradle
// 在 core-common 中提供运行时库
api "com.squareup.moshi:moshi:$moshi_version"
api "com.squareup.moshi:moshi-kotlin:$moshi_version"
kapt "com.squareup.moshi:moshi-kotlin-codegen:$moshi_version"

// 在 app 模块中需要重新声明 kapt
implementation project(":core-common")  // 获得运行时库
kapt "com.squareup.moshi:moshi-kotlin-codegen:$moshi_version"  // 启用注解处理
```

### 命名规范

- 使用下划线分隔：`androidx_core_version`
- 按库分组：`retrofit_version`, `okhttp_version`
- 避免过长名称：使用 `lifecycle_version` 而不是 `androidx_lifecycle_viewmodel_version`

### 版本兼容性

升级版本时注意：
- 主版本号变化可能包含破坏性改动
- 查看官方 CHANGELOG 和迁移指南
- 在 CI/CD 中运行完整测试套件

## 📚 最佳实践

1. **定期维护**：每月检查并更新依赖版本
2. **测试优先**：升级后立即运行测试
3. **文档更新**：重大版本升级时更新相关文档
4. **渐进升级**：优先升级补丁版本，谨慎升级主版本

## 🛠️ 常用命令

```bash
# 查看项目依赖树
./gradlew :app:dependencies

# 检查依赖更新
./gradlew dependencyUpdates

# 清理并重新构建
./gradlew clean build
```

## 📞 问题反馈

如有版本管理相关问题，请：
1. 检查 `versions.gradle` 中是否已定义对应版本
2. 确认变量名拼写是否正确
3. 查看模块是否正确引用版本变量

