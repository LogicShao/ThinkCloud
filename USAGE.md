# ThinkCloud LLM Client - 使用指南

## 🚀 快速开始

### 1. 运行应用

```bash
# 方式 1: 使用 Android Studio
# 打开项目后，点击 Run 按钮（绿色三角形）

# 方式 2: 使用命令行
./gradlew installDebug
```

### 2. 配置 API 密钥

1. **启动应用后**，点击右上角的**设置图标**（齿轮图标）
2. 输入你的 API 密钥：
   - **DeepSeek**: 从 [platform.deepseek.com](https://platform.deepseek.com) 获取
   - **Kimi**: 从 [platform.moonshot.cn](https://platform.moonshot.cn) 获取
   - **通义千问**: 从 [dashscope.aliyun.com](https://dashscope.aliyun.com) 获取（暂未完整实现）

3. 点击**保存**按钮
4. 返回聊天界面（点击返回按钮）

### 3. 开始对话

1. 在聊天界面顶部选择**供应商**（DeepSeek 或 Kimi）
2. 选择**模型**（如 deepseek-chat、moonshot-v1-8k 等）
3. 在底部输入框输入消息
4. 点击**发送按钮**开始对话

## ✨ 功能特性

### 已实现功能

✅ **多供应商支持**
- DeepSeek（deepseek-chat, deepseek-coder, deepseek-reasoner）
- Kimi（moonshot-v1-8k, moonshot-v1-32k, moonshot-v1-128k）

✅ **流式响应**
- 实时显示 AI 回复
- 支持打字机效果

✅ **安全存储**
- API 密钥使用 EncryptedSharedPreferences 加密存储
- 支持 Android Keystore

✅ **Material Design 3**
- 现代化 UI 设计
- 明暗主题自动切换

✅ **双界面**
- 聊天界面：消息展示和对话
- 配置界面：API 密钥管理

### 待完善功能

⏳ **通义千问 Provider**
- API 接口已定义，需要实现具体调用逻辑

⏳ **对话历史**
- 持久化存储对话记录
- 历史对话查看和管理

⏳ **多轮对话上下文**
- 自动维护对话上下文
- 上下文长度管理

## 🔧 开发说明

### 项目架构

```
app/
├── domain/              # 领域层（纯 Kotlin）
│   ├── model/          # 数据模型
│   └── repository/     # 仓库接口
├── data/               # 数据层
│   ├── local/         # 本地存储（API 密钥）
│   ├── remote/        # 网络请求
│   │   ├── api/       # Retrofit API Service
│   │   ├── provider/  # LLM Provider 实现
│   │   └── config/    # API 配置
│   └── repository/    # 仓库实现
├── ui/                # UI 层
│   ├── chat/         # 聊天界面
│   ├── config/       # 配置界面
│   └── theme/        # 主题配置
└── di/               # 依赖注入
```

### 关键类说明

#### Provider 层

- **LlmProvider**: 供应商接口，定义统一的 API 调用规范
- **DeepSeekProvider**: DeepSeek 实现，支持流式和非流式响应
- **KimiProvider**: Kimi 实现，使用 OpenAI 兼容 API
- **ProviderFactory**: 工厂模式管理 Provider 实例

#### Repository 层

- **ChatRepository**: 仓库接口
- **ChatRepositoryImpl**: 仓库实现，协调 Provider 调用

#### UI 层

- **ChatViewModel**: 聊天界面状态管理
- **ConfigViewModel**: 配置界面状态管理
- **ChatScreen**: 聊天界面 Composable
- **ConfigScreen**: 配置界面 Composable

### 添加新供应商

要添加新的 LLM 供应商，请参考 [CONTRIBUTING.md](../CONTRIBUTING.md) 中的详细说明。

## 🐛 调试技巧

### 查看网络日志

项目已集成 OkHttp Logging Interceptor，可以在 Logcat 中查看详细的 HTTP 请求和响应：

```
# Android Studio Logcat 过滤
标签: DeepSeekProvider, KimiProvider, OkHttp
```

### 常见问题

**Q: 提示"发送消息失败"**
- 检查网络连接
- 确认 API 密钥正确
- 查看 Logcat 错误日志

**Q: 流式响应不显示**
- 确认选择的模型支持流式输出
- 检查 request.stream 参数是否为 true

**Q: API 密钥保存失败**
- 确认设备支持 Android Keystore
- 检查存储权限

## 📝 测试建议

### 功能测试清单

1. **配置测试**
   - [ ] 输入 DeepSeek API 密钥并保存
   - [ ] 输入 Kimi API 密钥并保存
   - [ ] 检查保存成功提示
   - [ ] 重启应用，验证密钥加载

2. **对话测试**
   - [ ] 使用 DeepSeek 发送消息
   - [ ] 使用 Kimi 发送消息
   - [ ] 测试流式响应
   - [ ] 测试非流式响应
   - [ ] 测试错误处理

3. **UI 测试**
   - [ ] 测试明暗主题切换
   - [ ] 测试界面切换（聊天 ↔ 配置）
   - [ ] 测试消息滚动
   - [ ] 测试模型选择器

## 🔐 安全注意事项

1. **不要提交 API 密钥到代码仓库**
2. **API 密钥已加密存储，但仍需注意设备安全**
3. **生产环境建议实现服务端代理，避免直接暴露 API 密钥**

## 📚 相关文档

- [README.md](../README.md) - 项目介绍
- [CONTRIBUTING.md](../CONTRIBUTING.md) - 贡献指南
- [CHANGELOG.md](../CHANGELOG.md) - 变更日志
- [CLAUDE.md](../CLAUDE.md) - 架构文档

## 🤝 获取帮助

- **问题反馈**: [GitHub Issues](https://github.com/LogicShao/ThinkCloud/issues)
- **讨论交流**: [GitHub Discussions](https://github.com/LogicShao/ThinkCloud/discussions)

---

祝你使用愉快！ 🎉
