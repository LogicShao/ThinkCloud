[根目录](../CLAUDE.md) > **app**

# app 模块文档 - ThinkCloud LLM Client

## 模块职责

app 模块是 ThinkCloud LLM Client 的主应用模块，采用完整的 MVVM + Repository 架构，负责：

- 应用入口和生命周期管理
- 多供应商 LLM 对话功能实现
- UI 界面构建和用户交互
- 依赖注入配置管理
- 安全配置存储
- 对话历史持久化
- 主题系统管理
- 测试实现

## 架构分层

### 领域层 (Domain)

- **位置**: `domain/`
- **职责**: 定义业务模型和仓库接口
- **特点**: 纯 Kotlin，无 Android 依赖

### 数据层 (Data)

- **位置**: `data/`
- **职责**: 实现数据访问和网络通信
- **特点**: 实现 Repository 接口，处理多数据源

### UI 层 (UI)

- **位置**: `ui/`
- **职责**: 用户界面和状态管理
- **特点**: Jetpack Compose，遵循单向数据流

### 依赖注入 (DI)

- **位置**: `di/`
- **职责**: 依赖关系管理
- **特点**: 使用 Koin 框架

## 入口与启动

### 应用类 (LlmClientApp)

- **文件**: `src/main/java/com/thinkcloud/llmclient/LlmClientApp.kt`
- **职责**: 应用入口，初始化依赖注入

```kotlin
class LlmClientApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化 Koin 依赖注入
        startKoin {
            androidContext(this@LlmClientApp)
            modules(appModule)
        }
    }
}
```

### 主活动 (MainActivity)

- **文件**: `src/main/java/com/thinkcloud/llmclient/MainActivity.kt`
- **职责**: 应用主入口，设置 Compose UI，支持三界面导航
- **界面**: Chat（聊天）、Config（配置）、ConversationList（对话历史）

```kotlin
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
            val systemInDarkTheme = isSystemInDarkTheme()

            // 根据主题模式决定是否使用深色主题
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemInDarkTheme
            }

            ThinkCloudTheme(darkTheme = darkTheme) {
                // 三界面导航
                when (currentScreen) {
                    Screen.Chat -> ChatScreen(...)
                    Screen.Config -> ConfigScreen(...)
                    Screen.ConversationList -> ConversationListScreen(...)
                }
            }
        }
    }
}
```

## 领域模型

### 聊天消息 (ChatMessage)

- **文件**: `domain/model/ChatMessage.kt`
- **结构**:
    - `id`: 消息唯一标识
    - `content`: 消息内容
    - `role`: 消息角色 (USER/ASSISTANT/SYSTEM)
    - `timestamp`: 时间戳
    - `model`: 使用的模型名称
    - `provider`: 使用的供应商
    - `isStreaming`: 是否正在流式输出
    - `isError`: 是否是错误消息
    - `errorMessage`: 错误信息

### 对话会话 (Conversation)

- **文件**: `domain/model/Conversation.kt`
- **结构**:
    - `id`: 对话唯一标识
    - `title`: 对话标题
    - `createdAt`: 创建时间
    - `updatedAt`: 更新时间
    - `messageCount`: 消息数量
    - `messages`: 消息列表

### 主题模式 (ThemeMode)

- **文件**: `domain/model/ThemeMode.kt`
- **枚举值**:
    - `SYSTEM`: 跟随系统
    - `LIGHT`: 浅色模式
    - `DARK`: 深色模式
- **功能**: 支持从字符串转换

### LLM 请求与响应

- **文件**: `domain/model/LlmRequest.kt`
- **请求结构**:
    - `messages`: 消息列表
    - `model`: 模型名称
    - `maxTokens`: 最大 token 数
    - `temperature`: 温度参数
    - `topP`: Top-P 采样参数
    - `stream`: 是否启用流式输出
    - `provider`: 供应商类型

- **响应类型**:
    - `Success`: 成功响应
    - `Streaming`: 流式响应
    - `Error`: 错误响应

### 供应商类型 (LlmProviderType)

- **枚举**: DEEPSEEK, ALIBABA, KIMI
- **扩展性**: 预留支持智谱、百度、腾讯等供应商

## 数据层实现

### 仓库接口

#### ChatRepository

- **文件**: `domain/repository/ChatRepository.kt`
- **方法**:
    - `sendMessage()`: 发送消息到 LLM
    - `getSupportedModels()`: 获取支持的模型列表
    - `validateApiKey()`: 验证 API 密钥
    - `getCurrentProvider()`: 获取当前活跃供应商
    - `setCurrentProvider()`: 设置当前活跃供应商

#### ConversationRepository

- **文件**: `domain/repository/ConversationRepository.kt`
- **方法**:
    - `getAllConversations()`: 获取所有对话列表
    - `getConversationById()`: 根据 ID 获取对话（包含消息）
    - `saveConversation()`: 保存对话（新建或更新）
    - `deleteConversation()`: 删除对话
    - `deleteAllConversations()`: 删除所有对话

### 仓库实现

#### ChatRepositoryImpl

- **文件**: `data/repository/ChatRepositoryImpl.kt`
- **依赖**: ApiConfig, ProviderFactory
- **特性**: 通过工厂模式管理多供应商

#### ConversationRepositoryImpl

- **文件**: `data/repository/ConversationRepositoryImpl.kt`
- **依赖**: ConversationDao, MessageDao
- **特性**: Room 数据库操作，实体转换

### Room 数据库

#### AppDatabase

- **文件**: `data/local/database/AppDatabase.kt`
- **版本**: 1
- **实体**: ConversationEntity, MessageEntity
- **DAO**: ConversationDao, MessageDao
- **单例**: 使用双重检查锁定模式

#### 实体类

**ConversationEntity**
- **文件**: `data/local/entity/ConversationEntity.kt`
- **表名**: conversations
- **字段**: id, title, createdAt, updatedAt, messageCount

**MessageEntity**
- **文件**: `data/local/entity/MessageEntity.kt`
- **表名**: messages
- **外键**: conversationId -> conversations.id (级联删除)
- **索引**: conversationId
- **字段**: id, conversationId, content, role, timestamp, model, provider, isError, errorMessage

#### DAO 接口

**ConversationDao**
- **文件**: `data/local/dao/ConversationDao.kt`
- **操作**: 查询、插入、更新、删除对话
- **Flow**: 实时监听数据变化

**MessageDao**
- **文件**: `data/local/dao/MessageDao.kt`
- **操作**: 查询、插入、删除消息
- **Flow**: 按对话 ID 查询消息列表

### 供应商接口 (LlmProvider)

- **文件**: `data/remote/provider/LlmProvider.kt`
- **统一接口**: 所有供应商实现必须遵循
- **方法**:
    - `sendMessage()`: 发送消息
    - `getSupportedModels()`: 获取模型列表
    - `validateApiKey()`: 验证 API 密钥
    - `isAvailable()`: 检查供应商是否可用
    - `getDisplayName()`: 获取显示名称
    - `getDefaultModel()`: 获取默认模型

### 供应商工厂 (ProviderFactory)

- **文件**: `data/remote/provider/ProviderFactory.kt`
- **职责**: 创建和管理不同的 LLM 供应商实例
- **支持供应商**: DeepSeek, 通义千问, Kimi
- **特性**: 缓存管理、配置更新、可用性检查

### 具体供应商实现

- **DeepSeekProvider**: 支持 deepseek-chat, deepseek-coder, deepseek-reasoner
- **AlibabaProvider**: 支持 qwen-turbo, qwen-plus, qwen-max, qwen-long, qwen-vl-plus
- **KimiProvider**: 支持 moonshot-v1-8k, moonshot-v1-32k, moonshot-v1-128k

## UI 层实现

### 聊天界面 (ChatScreen)

- **文件**: `ui/chat/ChatScreen.kt`
- **组件**:
    - 模型选择器 (ModelSelector)
    - 消息列表 (LazyColumn)
    - 消息气泡 (MessageBubble)
    - 消息输入框 (MessageInput)
    - 加载指示器
    - 设置按钮
    - 历史按钮
- **智能滚动**: 自动滚动到底部，支持手动滚动控制

### ChatViewModel

- **文件**: `ui/chat/ChatViewModel.kt`
- **依赖**: ChatRepository, ConversationRepository
- **状态管理**: 使用 MutableStateFlow
- **事件处理**:
    - 输入文本变化
    - 发送消息
    - 供应商选择
    - 新建对话
    - 加载对话
- **流式响应**: 实时更新流式消息内容
- **对话管理**: 自动保存对话和消息

### ChatState

- **文件**: `ui/chat/state/ChatState.kt`
- **包含**:
    - `messages`: 消息列表
    - `inputText`: 输入文本
    - `isLoading`: 加载状态
    - `selectedProvider`: 选中的供应商
    - `selectedModel`: 选中的模型
    - `isStreaming`: 是否正在流式输出
    - `errorMessage`: 错误信息
    - `currentConversationId`: 当前对话 ID

### 对话历史列表界面 (ConversationListScreen)

- **文件**: `ui/conversation/ConversationListScreen.kt`
- **组件**:
    - 对话列表 (LazyColumn)
    - 对话列表项 (ConversationItem)
    - 新建按钮 (FloatingActionButton)
    - 空状态提示
- **交互**:
    - 点击对话加载历史
    - 滑动删除对话
    - 时间戳自动格式化

### ConversationListViewModel

- **文件**: `ui/conversation/ConversationListViewModel.kt`
- **功能**:
    - 加载对话列表
    - 删除对话
    - 错误处理

### ConversationListState

- **文件**: `ui/conversation/state/ConversationListState.kt`
- **包含**:
    - `conversations`: 对话列表
    - `isLoading`: 加载状态
    - `errorMessage`: 错误信息

### 配置界面 (ConfigScreen)

- **文件**: `ui/config/ConfigScreen.kt`
- **组件**:
    - 主题选择器 (ThemeSelector)
    - API 密钥输入框 (ApiKeyInput)
    - 保存按钮 (SaveButton)
    - 供应商状态显示
    - 说明文本

### ConfigViewModel

- **文件**: `ui/config/ConfigViewModel.kt`
- **依赖**: SecureConfigManager, ProviderFactory, ThemeViewModel
- **功能**:
    - 加载和保存 API 密钥
    - 主题模式管理
    - 验证供应商可用性
    - 状态管理和错误处理

### ConfigState

- **文件**: `ui/config/state/ConfigState.kt`
- **包含**:
    - 各供应商 API 密钥
    - 主题模式
    - 加载和保存状态
    - 供应商可用性状态
    - 错误信息

### 主题系统

#### ThemeViewModel

- **文件**: `ui/theme/ThemeViewModel.kt`
- **依赖**: SecureConfigManager
- **功能**:
    - 加载主题配置
    - 更新主题模式
    - 状态管理
- **生命周期**: 全局单例（通过 Koin single）

#### ThemeSelector

- **文件**: `ui/config/components/ThemeSelector.kt`
- **功能**: Radio 按钮组选择主题模式
- **选项**: 跟随系统、浅色、深色
- **样式**: Material Design 3 Card

### UI 组件

#### 消息气泡 (MessageBubble)

- **文件**: `ui/chat/components/MessageBubble.kt`
- **特性**:
    - 区分用户和 AI 消息样式
    - 支持错误消息显示
    - 显示模型信息

#### 消息输入框 (MessageInput)

- **文件**: `ui/chat/components/MessageInput.kt`
- **特性**:
    - 支持多行文本输入
    - 发送按钮状态管理
    - 加载状态禁用

#### 模型选择器 (ModelSelector)

- **文件**: `ui/chat/components/ModelSelector.kt`
- **特性**:
    - 供应商下拉选择
    - 模型下拉选择
    - 动态更新模型列表

#### API 密钥输入框 (ApiKeyInput)

- **文件**: `ui/config/components/ApiKeyInput.kt`
- **特性**:
    - 供应商名称显示
    - 安全输入（密码类型）
    - 可用性状态指示

#### 保存按钮 (SaveButton)

- **文件**: `ui/config/components/SaveButton.kt`
- **特性**:
    - 加载状态显示
    - 成功状态反馈
    - 禁用状态管理

## 安全配置

### 安全配置管理器 (SecureConfigManager)

- **文件**: `data/local/config/SecureConfigManager.kt`
- **技术**: Android Keystore + EncryptedSharedPreferences
- **功能**:
    - 安全存储 API 密钥
    - 主题模式存储
    - 支持多供应商密钥管理
    - 密钥验证和清除
    - 可用性检查

### API 配置 (ApiConfig)

- **文件**: `data/remote/config/ApiConfig.kt`
- **配置项**:
    - 各供应商 API 密钥
    - 基础 URL 配置
    - 可用性检查
    - 供应商数量统计

## 依赖注入

### 应用模块 (AppModule)

- **文件**: `di/AppModule.kt`
- **配置**:
    - Context 注入
    - SecureConfigManager
    - ApiConfig
    - ProviderFactory
    - AppDatabase
    - ChatRepository
    - ConversationRepository
    - ThemeViewModel（全局单例）
    - ChatViewModel
    - ConfigViewModel
    - ConversationListViewModel

## 关键依赖与配置

### 构建配置 (`build.gradle.kts`)

- **命名空间**: `com.thinkcloud.llmclient`
- **编译 SDK**: 36
- **最小 SDK**: 24
- **目标 SDK**: 36
- **版本**: 1.0 (code: 1)

### 主要依赖

- **Compose**: androidx.compose 相关依赖
- **网络**: Retrofit, OkHttp, 日志拦截器
- **协程**: kotlinx-coroutines-android
- **生命周期**: androidx.lifecycle 相关
- **依赖注入**: Koin Android 和 Compose 扩展
- **安全加密**: androidx.security.crypto
- **数据库**: Room Runtime, Room KTX, Room Compiler (kapt)

## 测试与质量

### 单元测试

- **位置**: `src/test/java/com/thinkcloud/llmclient/`
- **框架**: JUnit 4
- **覆盖范围**: ViewModel 逻辑、仓库实现

### 仪器化测试

- **位置**: `src/androidTest/java/com/thinkcloud/llmclient/`
- **框架**: AndroidJUnit4
- **覆盖范围**: UI 交互、集成测试

## UI 主题系统

### 颜色定义 (`Color.kt`)

- 支持明暗两种主题
- Material Design 3 颜色系统

### 主题实现 (`Theme.kt`)

- 支持动态颜色（Android 12+）
- 自动跟随系统深色模式
- 通过 ThemeViewModel 控制

### 排版系统 (`Type.kt`)

- 基础文本样式定义
- 可扩展的排版配置

## 常见问题 (FAQ)

### Q: 如何添加新的 LLM 供应商？

A: 1. 在 LlmProviderType 枚举中添加新类型
2. 实现 LlmProvider 接口
3. 在 ProviderFactory 中添加创建逻辑
4. 在 ApiConfig 中添加配置项
5. 在 SecureConfigManager 中添加密钥存储方法

### Q: 如何配置 API 密钥？

A: 通过设置界面输入各供应商的 API 密钥，系统会自动验证并保存到安全存储

### Q: 如何实现流式响应？

A: 使用 Flow 处理流式数据，在 ViewModel 中实时更新消息内容

### Q: 如何自定义主题？

A: 通过配置界面的主题选择器，选择浅色/深色/跟随系统

### Q: 如何管理对话历史？

A: 点击聊天界面的历史按钮，可查看、加载、删除对话历史

### Q: 对话数据存储在哪里？

A: 使用 Room 数据库本地存储，数据文件为 `thinkcloud_database`

### Q: 如何切换界面？

A:
- 聊天界面点击设置按钮 -> 配置界面
- 聊天界面点击历史按钮 -> 对话历史列表
- 配置界面/对话历史列表点击返回按钮 -> 聊天界面

### Q: 智能滚动如何工作？

A: 当用户在消息列表底部时自动滚动到最新消息，当用户手动滚动时暂停自动滚动，提供滚动到底部按钮

## 相关文件清单

### 领域层文件

- `domain/model/ChatMessage.kt` - 聊天消息模型
- `domain/model/Conversation.kt` - 对话会话模型
- `domain/model/ThemeMode.kt` - 主题模式枚举
- `domain/model/LlmRequest.kt` - LLM 请求响应模型
- `domain/repository/ChatRepository.kt` - 聊天仓库接口
- `domain/repository/ConversationRepository.kt` - 对话仓库接口

### 数据层文件

#### 仓库实现
- `data/repository/ChatRepositoryImpl.kt` - 聊天仓库实现
- `data/repository/ConversationRepositoryImpl.kt` - 对话仓库实现

#### 远程数据源
- `data/remote/provider/LlmProvider.kt` - 供应商接口
- `data/remote/provider/ProviderFactory.kt` - 供应商工厂
- `data/remote/provider/DeepSeekProvider.kt` - DeepSeek 实现
- `data/remote/provider/AlibabaProvider.kt` - 通义千问实现
- `data/remote/provider/KimiProvider.kt` - Kimi 实现
- `data/remote/config/ApiConfig.kt` - API 配置

#### 本地数据源
- `data/local/config/SecureConfigManager.kt` - 安全配置管理器
- `data/local/database/AppDatabase.kt` - Room 数据库
- `data/local/entity/ConversationEntity.kt` - 对话实体
- `data/local/entity/MessageEntity.kt` - 消息实体
- `data/local/dao/ConversationDao.kt` - 对话 DAO
- `data/local/dao/MessageDao.kt` - 消息 DAO

### UI 层文件

#### 聊天界面
- `ui/chat/ChatScreen.kt` - 聊天界面
- `ui/chat/ChatViewModel.kt` - ViewModel
- `ui/chat/state/ChatState.kt` - 状态定义
- `ui/chat/components/MessageBubble.kt` - 消息气泡
- `ui/chat/components/MessageInput.kt` - 消息输入框
- `ui/chat/components/ModelSelector.kt` - 模型选择器

#### 配置界面
- `ui/config/ConfigScreen.kt` - 配置界面
- `ui/config/ConfigViewModel.kt` - 配置 ViewModel
- `ui/config/state/ConfigState.kt` - 配置状态
- `ui/config/components/ApiKeyInput.kt` - API 密钥输入框
- `ui/config/components/SaveButton.kt` - 保存按钮
- `ui/config/components/ThemeSelector.kt` - 主题选择器

#### 对话历史界面
- `ui/conversation/ConversationListScreen.kt` - 对话历史列表界面
- `ui/conversation/ConversationListViewModel.kt` - 对话列表 ViewModel
- `ui/conversation/state/ConversationListState.kt` - 对话列表状态

#### 主题系统
- `ui/theme/ThemeViewModel.kt` - 主题 ViewModel
- `ui/theme/Theme.kt` - 主题实现
- `ui/theme/Color.kt` - 颜色定义
- `ui/theme/Type.kt` - 排版系统

### 依赖注入文件

- `di/AppModule.kt` - 应用模块

### 入口文件

- `LlmClientApp.kt` - 应用类
- `MainActivity.kt` - 主活动

### 配置文件

- `build.gradle.kts` - 模块构建配置
- `AndroidManifest.xml` - 应用清单

## 变更记录 (Changelog)

### 2025-12-01 20:57:56

- 更新模块文档，添加对话历史持久化功能
- 新增 Room 数据库架构说明
- 新增主题系统文档（ThemeViewModel、ThemeSelector）
- 新增三界面导航说明（Chat/Config/ConversationList）
- 完善对话管理功能文档
- 更新依赖注入配置，包含数据库和主题管理
- 更新文件清单，包含新增的对话历史和主题相关文件

### 2025-11-28 23:56:17

- 更新模块文档，添加导航面包屑
- 完善智能滚动功能文档
- 更新文件清单和架构说明
- 添加常见问题解答

### 2025-11-28 19:18:27

- 更新模块文档，添加配置界面详细说明
- 完善双界面切换功能文档
- 添加配置 ViewModel 和状态管理说明
- 更新 UI 组件清单，包含配置界面组件

### 2025-11-28

- 更新模块文档，详细记录 LLM Client 架构
- 添加多供应商架构说明
- 完善安全配置和依赖注入文档
- 更新 UI 组件和状态管理说明

### 2025-11-27

- 模块文档初始创建
- 代码结构分析完成
- 测试策略文档化

---

*本文档由 Claude Code 自动生成，最后更新于 2025-12-01 20:57:56*
