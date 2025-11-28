# ThinkCloud LLM Client

<div align="center">

📱 **基于 Jetpack Compose 的多供应商 Android LLM 客户端**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blueviolet.svg?logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-24+-green.svg?logo=android)](https://developer.android.com)
[![Compose](https://img.shields.io/badge/Compose-Latest-blue.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

## 📖 简介

ThinkCloud LLM Client 是一个现代化的 Android 应用，为用户提供统一的 AI 对话体验。支持多个主流 LLM 供应商，采用 Material Design 3 设计规范和最新的 Android 开发技术栈。

### ✨ 核心特性

- 🎯 **多供应商支持** - 统一接口接入 DeepSeek、通义千问、Kimi 等多个 LLM
- 🔄 **流式响应** - 实时流式输出，提供流畅的对话体验
- 🔐 **安全存储** - 基于 Android Keystore 的 API 密钥加密存储
- 🎨 **Material Design 3** - 现代化 UI，支持明暗主题自动切换
- 🏗️ **Clean Architecture** - MVVM + Repository 分层架构，代码清晰可维护
- 🔌 **可扩展设计** - 插件化供应商接口，轻松添加新的 LLM 服务
- 📱 **双界面设计** - 聊天界面与配置界面无缝切换

## 🛠️ 技术栈

| 类别 | 技术 |
|------|------|
| **语言** | Kotlin |
| **UI 框架** | Jetpack Compose + Material Design 3 |
| **架构** | MVVM + Repository + Clean Architecture |
| **依赖注入** | Koin |
| **网络** | Retrofit + OkHttp |
| **安全** | Android Keystore + EncryptedSharedPreferences |
| **异步** | Kotlin Coroutines + Flow |
| **构建** | Gradle (Kotlin DSL) |

## 🚀 快速开始

### 环境要求

- Android Studio Flamingo 或更高版本
- JDK 11
- Android SDK 36 (API 36)
- Android 设备或模拟器（Android 7.0+）

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/LogicShao/ThinkCloud.git
   cd ThinkCloud
   ```

2. **打开项目**
   ```bash
   # 使用 Android Studio 打开项目
   studio .
   ```

3. **构建项目**
   ```bash
   ./gradlew build
   ```

4. **运行应用**
   - 连接 Android 设备或启动模拟器
   - 点击 Android Studio 的 Run 按钮
   - 或使用命令行：`./gradlew installDebug`

## ⚙️ 配置说明

### 首次使用

1. 启动应用后，点击右上角的**设置图标**进入配置界面
2. 输入你的 API 密钥（支持多个供应商）
3. 点击**保存**按钮
4. 返回聊天界面，选择供应商和模型开始对话

### API 密钥获取

| 供应商 | 获取地址 | 说明 |
|--------|---------|------|
| **DeepSeek** | [platform.deepseek.com](https://platform.deepseek.com) | 支持官方 API |
| **通义千问** | [dashscope.aliyun.com](https://dashscope.aliyun.com) | 阿里云 DashScope |
| **Kimi** | [platform.moonshot.cn](https://platform.moonshot.cn) | 月之暗面官方 API |

### 供应商配置示例

```kotlin
// API 密钥存储在安全的 EncryptedSharedPreferences 中
// 通过配置界面输入后自动加密保存
DeepSeek: sk-xxxxxxxxxxxxxxxx
通义千问: sk-xxxxxxxxxxxxxxxx
Kimi: sk-xxxxxxxxxxxxxxxx
```

## 🏛️ 架构概览

### 分层架构

```
┌─────────────────────────────────────────┐
│           UI Layer (Compose)            │
│  ┌─────────────┐      ┌──────────────┐ │
│  │ ChatScreen  │      │ ConfigScreen │ │
│  └─────────────┘      └──────────────┘ │
│         │                     │         │
│         ▼                     ▼         │
│  ┌─────────────────────────────────┐   │
│  │         ViewModel               │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│         Domain Layer (Pure Kotlin)      │
│  ┌──────────────────────────────────┐   │
│  │  Repository Interface + Models   │   │
│  └──────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           Data Layer                    │
│  ┌──────────────┐    ┌───────────────┐ │
│  │ Repository   │◄───┤ProviderFactory│ │
│  └──────────────┘    └───────────────┘ │
│         │                     │         │
│         ▼                     ▼         │
│  ┌──────────────────────────────────┐  │
│  │  DeepSeek │ Alibaba │ Kimi      │  │
│  │         LLM Providers             │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

### 核心组件

#### 1. UI 层
- **ChatScreen**: 聊天界面，包含消息列表、输入框、模型选择器
- **ConfigScreen**: 配置界面，管理 API 密钥
- **ViewModel**: 状态管理和业务逻辑协调

#### 2. Domain 层
- **ChatMessage**: 消息模型
- **LlmRequest/Response**: 请求响应模型
- **ChatRepository**: 仓库接口定义

#### 3. Data 层
- **ChatRepositoryImpl**: 仓库实现
- **ProviderFactory**: 供应商工厂模式
- **LlmProvider**: 统一的供应商接口
- **SecureConfigManager**: 安全配置管理

## 🌐 支持的 LLM 供应商

### DeepSeek

- `deepseek-chat` - 通用对话模型
- `deepseek-coder` - 代码专用模型
- `deepseek-reasoner` - 推理增强模型

### 通义千问（阿里云）

- `qwen-turbo` - 快速响应模型
- `qwen-plus` - 增强版模型
- `qwen-max` - 旗舰版模型
- `qwen-long` - 长文本模型
- `qwen-vl-plus` - 视觉增强模型

### Kimi（月之暗面）

- `moonshot-v1-8k` - 8K 上下文
- `moonshot-v1-32k` - 32K 上下文
- `moonshot-v1-128k` - 128K 上下文

## 📦 项目结构

```
ThinkCloud/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/thinkcloud/llmclient/
│   │   │   │   ├── domain/           # 领域层
│   │   │   │   │   ├── model/        # 数据模型
│   │   │   │   │   └── repository/   # 仓库接口
│   │   │   │   ├── data/             # 数据层
│   │   │   │   │   ├── remote/       # 网络请求
│   │   │   │   │   │   ├── provider/ # LLM 供应商实现
│   │   │   │   │   │   └── config/   # API 配置
│   │   │   │   │   ├── local/        # 本地存储
│   │   │   │   │   │   └── config/   # 安全配置
│   │   │   │   │   └── repository/   # 仓库实现
│   │   │   │   ├── ui/               # UI 层
│   │   │   │   │   ├── chat/         # 聊天界面
│   │   │   │   │   │   ├── components/ # UI 组件
│   │   │   │   │   │   └── state/     # 状态定义
│   │   │   │   │   ├── config/       # 配置界面
│   │   │   │   │   │   ├── components/
│   │   │   │   │   │   └── state/
│   │   │   │   │   └── theme/        # 主题系统
│   │   │   │   ├── di/               # 依赖注入
│   │   │   │   ├── LlmClientApp.kt   # 应用入口
│   │   │   │   └── MainActivity.kt   # 主活动
│   │   │   └── res/                  # 资源文件
│   │   ├── test/                     # 单元测试
│   │   └── androidTest/              # 仪器化测试
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── CLAUDE.md                         # 架构文档
└── README.md                         # 本文件
```

## 🔧 开发指南

### 添加新的 LLM 供应商

1. **创建供应商实现**

```kotlin
// data/remote/provider/NewProvider.kt
class NewProvider(
    private val apiKey: String
) : LlmProvider {
    override suspend fun sendMessage(request: LlmRequest): Flow<LlmResponse> {
        // 实现消息发送逻辑
    }

    override fun getSupportedModels(): List<String> {
        return listOf("model-1", "model-2")
    }

    // 实现其他接口方法...
}
```

2. **在枚举中添加类型**

```kotlin
// domain/model/LlmRequest.kt
enum class LlmProviderType {
    DEEPSEEK,
    ALIBABA,
    KIMI,
    NEW_PROVIDER  // 添加新供应商
}
```

3. **更新工厂类**

```kotlin
// data/remote/provider/ProviderFactory.kt
fun createProvider(type: LlmProviderType, apiConfig: ApiConfig): LlmProvider? {
    return when (type) {
        // ...现有代码
        LlmProviderType.NEW_PROVIDER -> {
            apiConfig.newProviderApiKey?.let { NewProvider(it) }
        }
    }
}
```

4. **更新配置管理**

```kotlin
// data/local/config/SecureConfigManager.kt
// 添加新的密钥存储方法
fun saveNewProviderApiKey(apiKey: String) { /* ... */ }
fun getNewProviderApiKey(): String? { /* ... */ }
```

### 编码规范

- 遵循 [Kotlin 官方编码约定](https://kotlinlang.org/docs/coding-conventions.html)
- 使用 4 空格缩进
- 类名使用 `PascalCase`
- 函数和变量名使用 `camelCase`
- 常量使用 `UPPER_SNAKE_CASE`

### Git 提交规范

```bash
# 格式
<type>(<scope>): <subject>

# 示例
feat(chat): 添加流式响应支持
fix(config): 修复 API 密钥验证问题
docs(readme): 更新快速开始指南
```

## 🧪 构建与测试

### 运行单元测试

```bash
./gradlew test
```

### 运行仪器化测试

```bash
./gradlew connectedAndroidTest
```

### 构建 Release 版本

```bash
./gradlew assembleRelease
```

### 代码检查

```bash
# Kotlin Lint
./gradlew ktlintCheck

# Android Lint
./gradlew lint
```

## 🤝 贡献指南

欢迎贡献代码、报告问题或提出建议！

1. **Fork 项目**
2. **创建特性分支** (`git checkout -b feature/AmazingFeature`)
3. **提交更改** (`git commit -m 'feat: add some amazing feature'`)
4. **推送到分支** (`git push origin feature/AmazingFeature`)
5. **开启 Pull Request**

### 贡献方向

- 🎯 添加新的 LLM 供应商支持
- 🐛 修复已知 Bug
- 📝 改进文档
- ✨ 提出新功能建议
- 🎨 优化 UI/UX 设计
- 🔧 性能优化

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📮 联系方式

- **项目主页**: [https://github.com/LogicShao/ThinkCloud](https://github.com/LogicShao/ThinkCloud)
- **问题反馈**: [Issues](https://github.com/LogicShao/ThinkCloud/issues)
- **讨论交流**: [Discussions](https://github.com/LogicShao/ThinkCloud/discussions)

## 🙏 致谢

感谢以下项目和服务：

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - 现代化的 Android UI 工具包
- [Koin](https://insert-koin.io/) - 轻量级依赖注入框架
- [Retrofit](https://square.github.io/retrofit/) - 类型安全的 HTTP 客户端
- [DeepSeek](https://www.deepseek.com/) - AI 技术提供商
- [阿里云](https://www.aliyun.com/) - 通义千问服务
- [月之暗面](https://www.moonshot.cn/) - Kimi AI 服务

## 🗺️ 路线图

- [ ] 添加对话历史持久化
- [ ] 实现多轮对话上下文管理
- [ ] 支持更多 LLM 供应商（智谱、百度、腾讯等）
- [ ] 添加语音输入功能
- [ ] 实现图片识别和生成
- [ ] 国际化支持
- [ ] 数据导出功能
- [ ] 自定义提示词模板

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给它一个 Star！⭐**

Made with ❤️ by ThinkCloud Team

</div>
