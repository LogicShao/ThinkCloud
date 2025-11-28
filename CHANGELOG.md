# 变更日志

本文件记录 ThinkCloud LLM Client 项目的所有重要变更。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [未发布]

### 计划中

- 添加对话历史持久化
- 实现多轮对话上下文管理
- 支持更多 LLM 供应商（智谱、百度、腾讯等）
- 添加语音输入功能
- 实现图片识别和生成
- 国际化支持
- 数据导出功能
- 自定义提示词模板

## [1.0.0] - 2025-11-28

### 新增

#### 核心功能
- ✨ 多供应商 LLM 支持
  - DeepSeek（deepseek-chat, deepseek-coder, deepseek-reasoner）
  - 通义千问（qwen-turbo, qwen-plus, qwen-max, qwen-long, qwen-vl-plus）
  - Kimi（moonshot-v1-8k, moonshot-v1-32k, moonshot-v1-128k）
- 🔄 流式响应支持，实时显示 AI 回复
- 💬 聊天界面，支持消息列表展示和输入
- ⚙️ 配置界面，管理多个供应商的 API 密钥
- 🔐 安全存储，基于 Android Keystore 加密保存 API 密钥

#### UI/UX
- 🎨 Material Design 3 设计规范
- 🌓 明暗主题自动切换
- 📱 双界面设计（聊天界面 + 配置界面）
- 🔀 模型选择器，支持供应商和模型动态切换
- 💭 消息气泡组件，区分用户和 AI 消息
- ⌨️ 消息输入框，支持多行文本输入

#### 架构
- 🏗️ MVVM + Repository + Clean Architecture
- 🔌 插件化供应商接口设计
- 💉 Koin 依赖注入
- 🔄 Kotlin Coroutines + Flow 异步处理
- 🌐 Retrofit + OkHttp 网络请求

#### 开发工具
- 📝 完整的项目文档（README、CONTRIBUTING、CLAUDE.md）
- 🧪 单元测试和仪器化测试框架
- 📋 Git 提交规范和分支策略
- 🔧 Gradle Kotlin DSL 构建配置

### 技术细节

#### 依赖项
- Jetpack Compose - UI 框架
- Kotlin 1.9+ - 开发语言
- Android SDK 36 - 目标平台
- Retrofit - 网络请求
- OkHttp - HTTP 客户端
- Koin - 依赖注入
- EncryptedSharedPreferences - 安全存储

#### 安全性
- 🔒 API 密钥加密存储
- 🔐 Android Keystore 集成
- 🌐 HTTPS 通信
- ✅ 证书验证

#### 性能
- ⚡ 流式响应优化
- 🎯 懒加载消息列表
- 💾 内存优化

### 已知问题

- 暂不支持对话历史持久化
- 暂不支持多轮对话上下文
- 仅支持文本对话，不支持图片

### 文档

- 📖 [README.md](README.md) - 项目介绍和快速开始
- 🤝 [CONTRIBUTING.md](CONTRIBUTING.md) - 贡献指南
- 🏛️ [CLAUDE.md](CLAUDE.md) - 架构文档
- 📄 [LICENSE](LICENSE) - MIT 许可证

---

## 版本说明

### 语义化版本格式

```
主版本号.次版本号.修订号
```

- **主版本号**：不兼容的 API 修改
- **次版本号**：向下兼容的功能性新增
- **修订号**：向下兼容的问题修正

### 变更类型

- **新增（Added）**：新功能
- **变更（Changed）**：现有功能的变更
- **废弃（Deprecated）**：即将移除的功能
- **移除（Removed）**：已移除的功能
- **修复（Fixed）**：Bug 修复
- **安全（Security）**：安全相关修复

---

## 链接

- [GitHub 仓库](https://github.com/LogicShao/ThinkCloud)
- [问题反馈](https://github.com/LogicShao/ThinkCloud/issues)
- [讨论区](https://github.com/LogicShao/ThinkCloud/discussions)

---

**注意**：本变更日志遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/) 规范，
并采用 [语义化版本](https://semver.org/lang/zh-CN/) 进行版本管理。
