# Markdown 渲染支持文档

## 概述

ThinkCloud LLM Client 现已支持在 LLM 回复中渲染 Markdown 格式内容,为用户提供更丰富的对话体验。

## 实现方案

### 技术选型

我们选择了 [multiplatform-markdown-renderer](https://github.com/mikepenz/multiplatform-markdown-renderer) 作为 Markdown 渲染库,理由如下:

1. **全面的 Markdown 支持**: 支持标题、列表、代码块、表格、图片、语法高亮等
2. **Material Design 3 集成**: 完美适配项目的 MD3 主题系统
3. **Kotlin Multiplatform**: 跨平台支持,未来扩展性强
4. **性能优化**: 针对 Compose 优化,渲染性能出色
5. **活跃维护**: 最新版本 0.38.1,持续更新

### 依赖配置

在 `gradle/libs.versions.toml` 中添加:

```toml
[versions]
markdownRenderer = "0.38.1"
coil = "3.0.4"

[libraries]
multiplatform-markdown-renderer = { group = "com.mikepenz", name = "multiplatform-markdown-renderer", version.ref = "markdownRenderer" }
multiplatform-markdown-renderer-m3 = { group = "com.mikepenz", name = "multiplatform-markdown-renderer-m3", version.ref = "markdownRenderer" }
multiplatform-markdown-renderer-coil3 = { group = "com.mikepenz", name = "multiplatform-markdown-renderer-coil3", version.ref = "markdownRenderer" }
coil-compose = { group = "io.coil-kt.coil3", name = "coil-compose", version.ref = "coil" }
```

在 `app/build.gradle.kts` 中引用:

```kotlin
dependencies {
    // Markdown 渲染
    implementation(libs.multiplatform.markdown.renderer)
    implementation(libs.multiplatform.markdown.renderer.m3)
    implementation(libs.multiplatform.markdown.renderer.coil3)
    implementation(libs.coil.compose)
}
```

### 核心组件

#### MarkdownContent 组件

位置: `app/src/main/java/com/thinkcloud/llmclient/ui/chat/components/MarkdownContent.kt`

```kotlin
@Composable
fun MarkdownContent(
    content: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography

    Markdown(
        content = content,
        modifier = modifier.padding(top = 4.dp),
        colors = markdownColor(text = textColor),
        typography = markdownTypography(
            h1 = typography.headlineLarge.copy(color = textColor),
            h2 = typography.headlineMedium.copy(color = textColor),
            h3 = typography.headlineSmall.copy(color = textColor),
            // ... 其他排版配置
        )
    )
}
```

**特性**:
- 自动适配消息气泡的文本颜色
- 遵循 Material Design 3 排版规范
- 支持主题切换(浅色/深色模式)

#### MessageBubble 集成

修改 `MessageBubble.kt` 中的消息内容渲染:

```kotlin
// 原代码:
Text(
    text = message.content,
    style = MaterialTheme.typography.bodyMedium,
    color = textColor,
    modifier = Modifier.padding(top = 4.dp)
)

// 新代码:
MarkdownContent(
    content = message.content,
    textColor = textColor
)
```

## 支持的 Markdown 语法

### 标题

```markdown
# H1 标题
## H2 标题
### H3 标题
#### H4 标题
##### H5 标题
###### H6 标题
```

### 文本样式

```markdown
**粗体文本**
*斜体文本*
~~删除线~~
`行内代码`
```

### 列表

```markdown
- 无序列表项 1
- 无序列表项 2
  - 嵌套列表项

1. 有序列表项 1
2. 有序列表项 2
   1. 嵌套有序列表
```

### 代码块

````markdown
```python
def hello_world():
    print("Hello, World!")
```
````

### 引用

```markdown
> 这是一段引用文本
> 可以跨多行
```

### 链接

```markdown
[链接文本](https://example.com)
```

### 表格

```markdown
| 列1 | 列2 | 列3 |
|-----|-----|-----|
| A   | B   | C   |
| D   | E   | F   |
```

## 技术升级

为了支持 Markdown 渲染,项目进行了以下技术升级:

### Kotlin 版本升级

- **从**: Kotlin 2.0.21
- **到**: Kotlin 2.1.0
- **原因**: Markdown 库使用 Kotlin 2.2 编译,需要更高版本的编译器支持

### Compose BOM 升级

- **从**: Compose BOM 2024.09.00
- **到**: Compose BOM 2025.12.00
- **原因**:
  - Markdown 库使用了较新的 Compose 运行时 API (`getCurrentCompositeKeyHashCode`)
  - 旧版本不包含这些 API,导致运行时崩溃
  - 新版本完全兼容 Kotlin 2.1.0

### Activity Compose 升级

- **从**: activity-compose 1.8.0
- **到**: activity-compose 1.9.3
- **原因**: 配合 Compose BOM 升级,确保 API 兼容性

### Room 数据库升级

- **从**: Room 2.6.1
- **到**: Room 2.7.1
- **原因**: Room 2.7.1 完全支持 Kotlin 2.0+

### KSP 迁移

- **从**: kapt (Kotlin Annotation Processing Tool)
- **到**: KSP (Kotlin Symbol Processing)
- **版本**: KSP 2.1.0-1.0.29
- **原因**:
  - kapt 不支持 Kotlin 2.0+ 语言版本
  - KSP 性能更好,是 Google 推荐的注解处理工具
  - Room 2.7+ 官方推荐使用 KSP

### 配置变更

在 `app/build.gradle.kts` 中:

```kotlin
// 原配置:
plugins {
    kotlin("kapt")
}
dependencies {
    kapt(libs.androidx.room.compiler)
}

// 新配置:
plugins {
    alias(libs.plugins.ksp)
}
dependencies {
    ksp(libs.androidx.room.compiler)
}
```

## 故障排除

### 问题 1: 应用崩溃 - NoSuchMethodError

**错误信息**:
```
java.lang.NoSuchMethodError: No static method getCurrentCompositeKeyHashCode
```

**原因**: Compose BOM 版本过旧,不包含 Markdown 库所需的新 API

**解决方案**: 升级 Compose BOM 到 2025.12.00 或更高版本

### 问题 2: Kotlin 编译错误 - 不兼容的元数据版本

**错误信息**:
```
Module was compiled with an incompatible version of Kotlin.
The binary version of its metadata is 2.2.0, expected version is 2.0.0
```

**原因**: Kotlin 版本过低,无法读取新库的元数据

**解决方案**: 升级 Kotlin 到 2.1.0 或更高版本

### 问题 3: Room 编译失败 - kapt 不支持 Kotlin 2.0+

**错误信息**:
```
Kapt currently doesn't support language version 2.0+
```

**原因**: kapt 注解处理器不支持 Kotlin 2.0+ 语言版本

**解决方案**:
1. 升级 Room 到 2.7.1
2. 从 kapt 迁移到 KSP

## 测试建议

### 测试用例

1. **基础文本**: 纯文本消息应正常显示
2. **标题**: 各级标题应有明显的大小和样式区分
3. **代码块**: 代码应使用等宽字体,带背景色
4. **列表**: 有序和无序列表应正确缩进和标记
5. **混合内容**: 包含多种 Markdown 元素的复杂消息
6. **主题切换**: 在浅色和深色主题下都应有良好的可读性
7. **流式响应**: Markdown 在流式输出时应能实时渲染

### 测试步骤

1. 启动应用,进入聊天界面
2. 向 LLM 发送问题,要求返回 Markdown 格式的回复
3. 观察回复内容的渲染效果
4. 切换主题模式,验证渲染适配性
5. 测试流式响应中的 Markdown 渲染

### 示例提示词

```
请用 Markdown 格式回复以下内容:
1. 一个 H1 标题
2. 几个 H2 和 H3 子标题
3. 带有粗体和斜体的文本
4. 一个 Python 代码示例
5. 一个有序列表和无序列表
6. 一个引用块
```

## 性能考虑

1. **渲染性能**: Markdown 库已针对 Compose 优化,重组开销较小
2. **内存使用**: 仅在需要时加载渲染组件,不影响整体内存占用
3. **流式更新**: 配合现有的流式响应节流机制(50ms),保证流畅体验

## 已知限制

1. **图片渲染**: 当前配置支持图片,但需要网络连接加载
2. **代码高亮**: 基础语法高亮支持,不包含复杂的语法分析
3. **表格**: 在小屏幕设备上可能需要横向滚动

## 未来优化方向

1. **自定义主题**: 为代码块添加更丰富的语法高亮配色方案
2. **性能监控**: 添加 Markdown 渲染性能的监控指标
3. **缓存优化**: 对频繁使用的 Markdown 内容进行渲染缓存
4. **数学公式**: 考虑添加 LaTeX 数学公式渲染支持
5. **图表支持**: 支持 Mermaid 等图表语法

## 参考资料

- [multiplatform-markdown-renderer GitHub](https://github.com/mikepenz/multiplatform-markdown-renderer)
- [multiplatform-markdown-renderer Maven Central](https://central.sonatype.com/artifact/com.mikepenz/multiplatform-markdown-renderer)
- [Coil 图片加载库](https://coil-kt.github.io/coil/)
- [Kotlin Symbol Processing (KSP)](https://kotlinlang.org/docs/ksp-overview.html)
- [Room 数据库文档](https://developer.android.com/jetpack/androidx/releases/room)

---

*文档创建于 2025-12-06*
