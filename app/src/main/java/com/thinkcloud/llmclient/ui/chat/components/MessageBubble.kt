package com.thinkcloud.llmclient.ui.chat.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thinkcloud.llmclient.domain.model.ChatMessage
import com.thinkcloud.llmclient.domain.model.MessageRole

@Composable
fun MessageBubble(
  message: ChatMessage,
  modifier: Modifier = Modifier
) {
  val isUser = message.role == MessageRole.USER
  val backgroundColor = if (isUser) {
    MaterialTheme.colorScheme.primary
  } else {
    MaterialTheme.colorScheme.surfaceVariant
  }
  val textColor = if (isUser) {
    MaterialTheme.colorScheme.onPrimary
  } else {
    MaterialTheme.colorScheme.onSurfaceVariant
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(backgroundColor)
        .padding(16.dp)
    ) {
      Column {
        // 消息角色标签
        Text(
          text = if (isUser) "你" else "AI",
          style = MaterialTheme.typography.labelSmall,
          color = textColor.copy(alpha = 0.7f),
          fontWeight = FontWeight.Bold
        )

        // 消息内容
        if (message.content.isEmpty() && message.isStreaming) {
          // 流式输入中，显示加载动画
          TypingIndicator(textColor = textColor)
        } else {
          Text(
            text = message.content,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier.padding(top = 4.dp)
          )

          // 流式输入指示器
          if (message.isStreaming && message.content.isNotEmpty()) {
            StreamingCursor(textColor = textColor)
          }
        }

        // 错误消息显示
        if (message.isError) {
          Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "错误",
              tint = Color.Red,
              modifier = Modifier.padding(end = 4.dp)
            )
            Text(
              text = message.errorMessage ?: "未知错误",
              style = MaterialTheme.typography.labelSmall,
              color = Color.Red
            )
          }
        }

        // 模型信息（如果是 AI 消息）
        if (!isUser && message.model != null) {
          Text(
            text = "模型: ${message.model}",
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
    }
  }
}

/**
 * 流式输入光标动画
 */
@Composable
private fun StreamingCursor(textColor: Color) {
  val infiniteTransition = rememberInfiniteTransition(label = "cursor")
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "cursorAlpha"
  )

  Text(
    text = "▋",
    style = MaterialTheme.typography.bodyMedium,
    color = textColor,
    modifier = Modifier
      .alpha(alpha)
      .padding(start = 2.dp)
  )
}

/**
 * 打字中指示器动画（三个点）
 */
@Composable
private fun TypingIndicator(textColor: Color) {
  val infiniteTransition = rememberInfiniteTransition(label = "typing")

  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier.padding(top = 4.dp)
  ) {
    repeat(3) { index ->
      val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(600, delayMillis = index * 200, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse
        ),
        label = "dot$index"
      )

      Box(
        modifier = Modifier
          .size(8.dp)
          .alpha(alpha)
          .clip(CircleShape)
          .background(textColor)
      )
    }
  }
}