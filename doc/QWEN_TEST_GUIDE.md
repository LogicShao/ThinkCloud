# 通义千问（Qwen）对话功能测试指南

## 🎯 功能概述

已成功实现通义千问（Qwen）的完整对话功能，包括：
- ✅ 非流式对话响应
- ✅ 流式对话响应（实时显示）
- ✅ 支持所有 Qwen 模型（qwen-turbo、qwen-plus、qwen-max、qwen-long、qwen-vl-plus）
- ✅ 完整的错误处理
- ✅ Token 使用统计
- ✅ 与现有架构完全集成

## 🏗️ 实现架构

### 新增文件
1. **`AlibabaApiModels.kt`** - 阿里云 OpenAI 兼容模式 API 数据模型
2. **`AlibabaApiService.kt`** - 阿里云 API 服务接口
3. **`QwenProvider.kt`** - 通义千问供应商实现（替换原有的简化版 AlibabaProvider）

### 更新文件
1. **`ProviderFactory.kt`** - 更新为使用 QwenProvider
2. **`ApiConfig.kt`** - 更新阿里云基础 URL 为 OpenAI 兼容模式
3. **文档链接** - 更新了相关文档中的链接

## 🔧 技术实现细节

### 1. API 兼容性
- 使用阿里云 DashScope 的 **OpenAI 兼容模式**
- 基础 URL: `https://dashscope.aliyuncs.com/compatible-mode/v1/`
- 接口路径: `/chat/completions`
- 认证方式: `Authorization: Bearer {api-key}`

### 2. 请求/响应格式
- 完全兼容 OpenAI API 格式
- 支持流式和非流式响应
- 统一的错误处理机制

### 3. 核心特性
- **流式响应**: 实时显示 AI 回复，支持打字机效果
- **错误处理**: 完整的 HTTP 错误码和错误消息处理
- **Token 统计**: 显示输入/输出 Token 使用情况
- **模型支持**: 支持所有 Qwen 系列模型

## 🧪 测试步骤

### 步骤 1: 获取 API 密钥
1. 访问 [阿里云 DashScope](https://dashscope.aliyuncs.com/)
2. 注册/登录阿里云账号
3. 开通 DashScope 服务
4. 在控制台获取 API 密钥（格式: `sk-xxxxxxxxxxxxxxxx`）

### 步骤 2: 配置应用
1. 运行 ThinkCloud LLM Client 应用
2. 进入配置界面（右上角齿轮图标）
3. 在"通义千问"部分输入 API 密钥
4. 点击"保存"按钮

### 步骤 3: 测试非流式对话
1. 返回聊天界面
2. 选择供应商: **通义千问**
3. 选择模型: **qwen-turbo**（推荐测试用）
4. 关闭"流式响应"开关
5. 发送测试消息（如"你好"）
6. 验证:
   - ✅ 收到完整的 AI 回复
   - ✅ 无流式效果，一次性显示
   - ✅ 查看 Logcat 中的 QwenProvider 日志

### 步骤 4: 测试流式对话
1. 确保选择供应商: **通义千问**
2. 打开"流式响应"开关
3. 发送测试消息（如"介绍一下你自己"）
4. 验证:
   - ✅ 实时显示 AI 回复，逐字出现
   - ✅ 有打字机光标动画
   - ✅ 查看 Logcat 中的流式处理日志

### 步骤 5: 测试不同模型
1. 依次测试所有支持的模型:
   - `qwen-turbo` - 快速响应
   - `qwen-plus` - 增强版
   - `qwen-max` - 旗舰版
   - `qwen-long` - 长文本版
2. 验证每个模型都能正常响应

## 📊 日志验证

### 关键日志标签
- `QwenProvider` - 通义千问供应商日志
- `OkHttp` - 网络请求详情

### 预期日志输出
```
D/QwenProvider: 开始发送消息到通义千问
D/QwenProvider: 模型: qwen-turbo, 流式: true, 消息数: 1
D/QwenProvider: API密钥长度: 64
D/QwenProvider: 使用流式响应模式
D/QwenProvider: 发起流式API请求
D/QwenProvider: 收到流式API响应: code=200, success=true
D/QwenProvider: 开始处理流式数据
D/QwenProvider: 已接收10个chunk，当前总长度: 150
D/QwenProvider: 流式数据接收完成，共45个chunk，总长度: 500
```

### 错误日志示例
```
E/QwenProvider: API请求失败: 401 - {"code":"InvalidAccessKeyId","message":"无效的AccessKey ID"}
E/QwenProvider: API请求失败: 429 - {"code":"Throttling","message":"请求频率超限"}
```

## 🐛 常见问题排查

### 问题 1: API 密钥无效
**症状**: HTTP 401 错误
**解决方案**:
1. 确认 API 密钥格式正确（以 `sk-` 开头）
2. 确认 DashScope 服务已开通
3. 确认 API 密钥有足够的余额

### 问题 2: 请求频率超限
**症状**: HTTP 429 错误
**解决方案**:
1. 降低请求频率
2. 升级 DashScope 服务套餐
3. 添加请求间隔

### 问题 3: 网络连接失败
**症状**: 超时或网络错误
**解决方案**:
1. 检查网络连接
2. 确认可以访问阿里云服务
3. 检查防火墙设置

### 问题 4: 流式显示不正常
**症状**: 一次性显示或显示卡顿
**解决方案**:
1. 检查 Logcat 中的流式日志
2. 确认网络速度足够
3. 调整流式节流参数（ChatViewModel 中）

## 🔍 高级测试

### 压力测试
1. 连续发送多条消息
2. 测试长文本对话
3. 测试并发请求

### 边界测试
1. 空消息测试
2. 超长消息测试
3. 特殊字符测试

### 性能测试
1. 响应时间测量
2. Token 使用效率
3. 内存使用情况

## 📝 测试报告模板

### 测试环境
- 设备型号: [填写]
- Android 版本: [填写]
- 网络环境: [WiFi/4G/5G]
- 应用版本: [填写]

### 测试结果
| 测试项 | 结果 | 备注 |
|--------|------|------|
| API 密钥配置 | ✅/❌ | |
| 非流式对话 | ✅/❌ | |
| 流式对话 | ✅/❌ | |
| 模型切换 | ✅/❌ | |
| 错误处理 | ✅/❌ | |
| Token 统计 | ✅/❌ | |

### 发现的问题
1. [问题描述]
2. [问题描述]

### 建议改进
1. [改进建议]
2. [改进建议]

## 🎉 成功标准

### 基本功能
- [ ] API 密钥可以正常保存和加载
- [ ] 可以选择通义千问供应商
- [ ] 可以切换不同 Qwen 模型
- [ ] 非流式对话正常响应
- [ ] 流式对话实时显示

### 高级功能
- [ ] 错误信息友好显示
- [ ] Token 使用统计正确
- [ ] 网络中断后可以重试
- [ ] 长时间对话稳定

### 用户体验
- [ ] 响应速度可接受
- [ ] 流式效果流畅
- [ ] 界面无卡顿
- [ ] 错误提示清晰

## 🔗 相关资源

### 官方文档
- [阿里云 DashScope 文档](https://help.aliyun.com/zh/model-studio/developer-reference/compatibility-of-openai-with-dashscope)
- [通义千问模型介绍](https://help.aliyun.com/zh/model-studio/user-guide/model-introduction)

### 测试工具
- [Postman 集合](https://www.postman.com/) - API 测试
- [Charles Proxy](https://www.charlesproxy.com/) - 网络调试
- [Android Studio Logcat](https://developer.android.com/studio/debug/logcat) - 日志查看

### 支持渠道
- [阿里云工单系统](https://workorder.console.aliyun.com/)
- [GitHub Issues](https://github.com/LogicShao/ThinkCloud/issues)
- [项目文档](../CLAUDE.md)

---

**最后更新**: 2025-12-02
**测试状态**: 待测试（需要有效 API 密钥）
**负责人**: [填写测试人员]

*注意：实际测试需要有效的阿里云 DashScope API 密钥*