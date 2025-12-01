package com.thinkcloud.llmclient.ui.conversation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thinkcloud.llmclient.domain.model.Conversation
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 对话历史列表界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
  onBackClick: () -> Unit,
  onNewConversation: () -> Unit,
  onConversationClick: (String) -> Unit,
  viewModel: ConversationListViewModel = koinViewModel()
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  // 显示错误消息
  LaunchedEffect(state.errorMessage) {
    state.errorMessage?.let { error ->
      snackbarHostState.showSnackbar(error)
      viewModel.clearError()
    }
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(
            text = "对话历史",
            style = MaterialTheme.typography.titleLarge
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "返回"
            )
          }
        }
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onNewConversation,
        containerColor = MaterialTheme.colorScheme.primary
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "新建对话"
        )
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { paddingValues ->
    if (state.isLoading) {
      // 加载状态
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    } else if (state.conversations.isEmpty()) {
      // 空状态
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "暂无对话历史",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "点击右下角按钮开始新对话",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      // 对话列表
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(
          items = state.conversations,
          key = { it.id }
        ) { conversation ->
          ConversationItem(
            conversation = conversation,
            onClick = { onConversationClick(conversation.id) },
            onDelete = { viewModel.deleteConversation(conversation.id) }
          )
        }
      }
    }
  }
}

/**
 * 对话列表项
 */
@Composable
private fun ConversationItem(
  conversation: Conversation,
  onClick: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = conversation.title,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "${conversation.messageCount} 条消息",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = "•",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = formatTimestamp(conversation.updatedAt),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(onClick = onDelete) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "删除对话",
          tint = MaterialTheme.colorScheme.error
        )
      }
    }
  }
}

/**
 * 格式化时间戳
 */
private fun formatTimestamp(timestamp: Long): String {
  val now = System.currentTimeMillis()
  val diff = now - timestamp

  return when {
    diff < 60_000 -> "刚刚"
    diff < 3600_000 -> "${diff / 60_000} 分钟前"
    diff < 86400_000 -> "${diff / 3600_000} 小时前"
    diff < 604800_000 -> "${diff / 86400_000} 天前"
    else -> {
      val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
      sdf.format(Date(timestamp))
    }
  }
}
