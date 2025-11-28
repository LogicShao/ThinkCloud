# 贡献指南

首先，感谢你愿意为 ThinkCloud LLM Client 做出贡献！🎉

本文档提供了参与项目开发的指南和规范，帮助你更好地参与到项目中来。

## 📋 目录

- [行为准则](#行为准则)
- [如何贡献](#如何贡献)
- [开发环境设置](#开发环境设置)
- [分支策略](#分支策略)
- [提交规范](#提交规范)
- [代码规范](#代码规范)
- [测试要求](#测试要求)
- [Pull Request 流程](#pull-request-流程)
- [问题报告](#问题报告)
- [功能建议](#功能建议)

## 🤝 行为准则

### 我们的承诺

为了营造开放和包容的环境，我们承诺：

- 使用友好和包容的语言
- 尊重不同的观点和经验
- 优雅地接受建设性批评
- 关注对社区最有利的事情
- 对其他社区成员表示同理心

### 不可接受的行为

- 使用性别化语言或图像，以及不受欢迎的性关注或示好
- 挑衅、侮辱或贬损的评论，以及人身或政治攻击
- 公开或私下的骚扰
- 未经明确许可，发布他人的私人信息
- 其他在专业环境中可以合理认为不适当的行为

## 💡 如何贡献

### 贡献类型

我们欢迎以下类型的贡献：

1. **🐛 Bug 修复** - 修复已知问题
2. **✨ 新功能** - 实现新的功能特性
3. **📝 文档改进** - 改进文档质量
4. **🎨 UI/UX 优化** - 改进用户界面和体验
5. **🔧 性能优化** - 提升应用性能
6. **🧪 测试** - 增加测试覆盖率
7. **🌐 国际化** - 添加多语言支持
8. **🔌 供应商支持** - 添加新的 LLM 供应商

## 🛠️ 开发环境设置

### 前置要求

- **Android Studio**: Flamingo 或更高版本
- **JDK**: 11
- **Android SDK**: API 36
- **Git**: 最新版本
- **Kotlin**: 1.9+

### 设置步骤

1. **Fork 项目**

   点击 GitHub 页面右上角的 "Fork" 按钮

2. **克隆仓库**

   ```bash
   git clone https://github.com/你的用户名/ThinkCloud.git
   cd ThinkCloud
   ```

3. **添加上游仓库**

   ```bash
   git remote add upstream https://github.com/LogicShao/ThinkCloud.git
   ```

4. **安装依赖**

   使用 Android Studio 打开项目，Gradle 会自动下载依赖

5. **验证环境**

   ```bash
   ./gradlew build
   ./gradlew test
   ```

## 🌿 分支策略

我们使用 Git Flow 工作流：

### 主要分支

- **`main`** - 主分支，包含稳定的生产代码
- **`develop`** - 开发分支，包含最新的开发代码

### 特性分支

从 `develop` 分支创建特性分支：

```bash
# 新功能
git checkout -b feature/功能描述 develop

# Bug 修复
git checkout -b fix/问题描述 develop

# 文档更新
git checkout -b docs/文档描述 develop

# 性能优化
git checkout -b perf/优化描述 develop

# 重构
git checkout -b refactor/重构描述 develop
```

### 分支命名规范

- `feature/` - 新功能
- `fix/` - Bug 修复
- `docs/` - 文档更新
- `style/` - 代码格式调整
- `refactor/` - 代码重构
- `perf/` - 性能优化
- `test/` - 测试相关
- `chore/` - 构建/工具相关

## 📝 提交规范

### Commit Message 格式

采用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

- **feat**: 新功能
- **fix**: Bug 修复
- **docs**: 文档更新
- **style**: 代码格式调整（不影响代码运行）
- **refactor**: 重构（既不是新功能也不是修复）
- **perf**: 性能优化
- **test**: 测试相关
- **build**: 构建系统或依赖更新
- **ci**: CI/CD 配置更新
- **chore**: 其他不修改源代码的更改
- **revert**: 回退之前的提交

### Scope 范围

- **chat**: 聊天功能
- **config**: 配置功能
- **provider**: LLM 供应商
- **ui**: 用户界面
- **api**: API 相关
- **security**: 安全相关
- **deps**: 依赖管理

### 示例

```bash
# 好的提交
feat(chat): 添加流式响应支持
fix(config): 修复 API 密钥验证失败问题
docs(readme): 更新安装步骤说明
perf(chat): 优化消息列表渲染性能
refactor(provider): 重构供应商工厂模式

# 不好的提交
update code
fix bug
修复问题
```

### 提交信息规范

- 使用中文或英文（保持一致）
- 首字母小写
- 不要以句号结尾
- 使用祈使语气（"添加"而不是"已添加"或"添加了"）
- 主题行不超过 50 个字符
- Body 每行不超过 72 个字符

## 🎨 代码规范

### Kotlin 代码规范

遵循 [Kotlin 官方编码约定](https://kotlinlang.org/docs/coding-conventions.html)：

#### 命名规范

```kotlin
// 类名 - PascalCase
class ChatViewModel { }

// 函数名 - camelCase
fun sendMessage() { }

// 变量名 - camelCase
val inputText = ""

// 常量 - UPPER_SNAKE_CASE
const val MAX_RETRIES = 3

// 私有属性 - 使用下划线前缀（可选）
private val _state = MutableStateFlow<ChatState>()
```

#### 代码格式

```kotlin
// 缩进使用 4 个空格
class Example {
    fun method() {
        if (condition) {
            doSomething()
        }
    }
}

// 链式调用换行
listOf(1, 2, 3)
    .map { it * 2 }
    .filter { it > 5 }
    .forEach { println(it) }

// 函数参数过多时换行
fun longFunctionName(
    parameter1: String,
    parameter2: Int,
    parameter3: Boolean
): ReturnType {
    // 函数体
}
```

#### 最佳实践

```kotlin
// ✅ 使用数据类
data class ChatMessage(val id: String, val content: String)

// ✅ 使用密封类表示状态
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: String) : UiState()
    data class Error(val message: String) : UiState()
}

// ✅ 使用 Flow 处理异步流
fun observeMessages(): Flow<List<ChatMessage>> = flow {
    // 实现
}

// ✅ 空值安全
val length = text?.length ?: 0

// ❌ 避免使用 !!
val length = text!!.length  // 不推荐
```

### Compose 规范

```kotlin
// ✅ 可组合函数使用 PascalCase
@Composable
fun ChatScreen() { }

// ✅ 参数使用 camelCase
@Composable
fun MessageBubble(
    message: ChatMessage,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 实现
}

// ✅ 状态提升
@Composable
fun ParentComponent() {
    var state by remember { mutableStateOf("") }
    ChildComponent(
        value = state,
        onValueChange = { state = it }
    )
}

// ✅ 使用 remember 保存状态
val scrollState = rememberScrollState()

// ✅ 副作用使用 LaunchedEffect
LaunchedEffect(key1) {
    // 副作用代码
}
```

### 架构规范

#### 分层原则

```
UI 层 -> Domain 层 -> Data 层
```

- **UI 层**: 只负责 UI 展示和用户交互
- **Domain 层**: 纯 Kotlin，无 Android 依赖
- **Data 层**: 处理数据访问和业务逻辑

#### 依赖方向

- UI 依赖 Domain
- Data 实现 Domain
- 层与层之间通过接口通信

## 🧪 测试要求

### 测试覆盖

- 新功能必须包含单元测试
- 重要的 UI 交互需要仪器化测试
- Bug 修复应包含回归测试

### 测试类型

#### 单元测试

```kotlin
// app/src/test/
class ChatViewModelTest {
    @Test
    fun `sendMessage should update state correctly`() {
        // Given
        val viewModel = ChatViewModel()

        // When
        viewModel.sendMessage("Hello")

        // Then
        assertEquals("Hello", viewModel.state.value.inputText)
    }
}
```

#### 仪器化测试

```kotlin
// app/src/androidTest/
@Test
fun chatScreen_sendMessage_displaysMessage() {
    composeTestRule.setContent {
        ChatScreen()
    }

    composeTestRule
        .onNodeWithTag("messageInput")
        .performTextInput("Hello")

    composeTestRule
        .onNodeWithTag("sendButton")
        .performClick()

    composeTestRule
        .onNodeWithText("Hello")
        .assertIsDisplayed()
}
```

### 运行测试

```bash
# 单元测试
./gradlew test

# 仪器化测试
./gradlew connectedAndroidTest

# 测试覆盖率
./gradlew jacocoTestReport
```

## 🔄 Pull Request 流程

### 提交前检查

- [ ] 代码符合代码规范
- [ ] 所有测试通过
- [ ] 添加了必要的测试
- [ ] 更新了相关文档
- [ ] Commit 信息符合规范
- [ ] 代码已经过自我审查
- [ ] 没有编译警告

### PR 标题格式

```
<type>(<scope>): <description>
```

示例：
```
feat(chat): 添加语音输入功能
fix(config): 修复密钥保存失败问题
docs(contributing): 完善贡献指南
```

### PR 描述模板

```markdown
## 变更类型
- [ ] Bug 修复
- [ ] 新功能
- [ ] 代码重构
- [ ] 性能优化
- [ ] 文档更新
- [ ] 其他

## 变更说明
简要描述此次变更的目的和内容。

## 相关 Issue
Closes #issue号

## 测试
描述如何测试这些变更。

## 截图（如适用）
添加截图说明变更效果。

## 检查清单
- [ ] 代码符合项目编码规范
- [ ] 所有测试通过
- [ ] 添加了必要的测试
- [ ] 更新了相关文档
- [ ] 无编译警告
```

### 审查流程

1. 提交 PR 后，等待维护者审查
2. 根据反馈进行修改
3. 所有讨论解决后，PR 会被合并
4. 合并后，你的贡献将出现在下一个版本中

## 🐛 问题报告

### 报告 Bug

使用 GitHub Issues 报告 Bug，请包含：

1. **Bug 描述** - 清晰简洁的描述
2. **复现步骤** - 详细的复现步骤
3. **期望行为** - 应该发生什么
4. **实际行为** - 实际发生了什么
5. **环境信息** - 设备型号、Android 版本、应用版本
6. **截图/日志** - 如果适用

### Bug 报告模板

```markdown
**Bug 描述**
清晰简洁地描述 Bug。

**复现步骤**
1. 打开应用
2. 点击 '...'
3. 输入 '...'
4. 看到错误

**期望行为**
应该发生什么。

**实际行为**
实际发生了什么。

**环境信息**
- 设备: [如 Pixel 6]
- Android 版本: [如 Android 13]
- 应用版本: [如 1.0.0]

**截图**
如果适用，添加截图。

**额外信息**
其他相关信息。
```

## 💡 功能建议

### 提出新功能

使用 GitHub Issues 提出功能建议，请包含：

1. **问题描述** - 这个功能解决什么问题
2. **解决方案** - 你期望的实现方式
3. **替代方案** - 考虑过的其他方案
4. **额外信息** - 其他相关信息

### 功能建议模板

```markdown
**问题描述**
清晰描述这个功能要解决的问题。

**期望的解决方案**
描述你期望的实现方式。

**替代方案**
描述你考虑过的其他方案。

**额外信息**
其他相关信息、截图、参考资料等。
```

## 📚 其他资源

- [项目架构文档](CLAUDE.md)
- [README](README.md)
- [变更日志](CHANGELOG.md)
- [Kotlin 编码约定](https://kotlinlang.org/docs/coding-conventions.html)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Jetpack Compose 指南](https://developer.android.com/jetpack/compose)

## 🙏 致谢

感谢所有为项目做出贡献的开发者！你们的贡献让 ThinkCloud 变得更好！

---

有任何问题？欢迎在 [Discussions](https://github.com/LogicShao/ThinkCloud/discussions) 中讨论！
