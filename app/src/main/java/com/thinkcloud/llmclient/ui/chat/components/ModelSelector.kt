package com.thinkcloud.llmclient.ui.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thinkcloud.llmclient.domain.model.LlmProviderType

@Composable
fun ModelSelector(
  selectedProvider: LlmProviderType,
  selectedModel: String,
  availableModels: List<String> = emptyList(),
  onProviderSelected: (LlmProviderType) -> Unit,
  onModelSelected: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var providerExpanded by remember { mutableStateOf(false) }
  var modelExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = "选择模型",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // 供应商选择器
      Column(modifier = Modifier.weight(1f)) {
        TextButton(
          onClick = { providerExpanded = true },
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(selectedProvider.getDisplayName())
        }
        DropdownMenu(
          expanded = providerExpanded,
          onDismissRequest = { providerExpanded = false }
        ) {
          LlmProviderType.values().forEach { provider ->
            DropdownMenuItem(
              text = { Text(provider.getDisplayName()) },
              onClick = {
                onProviderSelected(provider)
                providerExpanded = false
              }
            )
          }
        }
      }

      // 模型选择器
      Column(modifier = Modifier.weight(1f)) {
        TextButton(
          onClick = { modelExpanded = true },
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(selectedModel)
        }
        DropdownMenu(
          expanded = modelExpanded,
          onDismissRequest = { modelExpanded = false }
        ) {
          // 使用从状态传入的可用模型列表
          if (availableModels.isEmpty()) {
            DropdownMenuItem(
              text = { Text("暂无可用模型") },
              onClick = { modelExpanded = false }
            )
          } else {
            availableModels.forEach { model ->
              DropdownMenuItem(
                text = { Text(model) },
                onClick = {
                  onModelSelected(model)
                  modelExpanded = false
                }
              )
            }
          }
        }
      }
    }
  }
}

/**
 * 获取供应商的显示名称
 */
private fun LlmProviderType.getDisplayName(): String {
  return when (this) {
    LlmProviderType.DEEPSEEK -> "DeepSeek"
    LlmProviderType.ALIBABA -> "通义千问"
    LlmProviderType.KIMI -> "Kimi"
  }
}