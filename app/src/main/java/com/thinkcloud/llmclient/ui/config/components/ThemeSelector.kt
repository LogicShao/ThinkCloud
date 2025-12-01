package com.thinkcloud.llmclient.ui.config.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.thinkcloud.llmclient.domain.model.ThemeMode

/**
 * 主题选择器组件
 *
 * @param currentTheme 当前选中的主题模式
 * @param onThemeChanged 主题改变回调
 */
@Composable
fun ThemeSelector(
  currentTheme: ThemeMode,
  onThemeChanged: (ThemeMode) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // 标题
      Text(
        text = "外观主题",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      // 主题选项列表
      Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        ThemeMode.values().forEach { mode ->
          ThemeOption(
            theme = mode,
            selected = currentTheme == mode,
            onClick = { onThemeChanged(mode) }
          )
        }
      }
    }
  }
}

/**
 * 单个主题选项
 *
 * @param theme 主题模式
 * @param selected 是否选中
 * @param onClick 点击回调
 */
@Composable
private fun ThemeOption(
  theme: ThemeMode,
  selected: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .selectable(
        selected = selected,
        onClick = onClick,
        role = Role.RadioButton
      )
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    RadioButton(
      selected = selected,
      onClick = null // 由父组件处理点击
    )

    Text(
      text = theme.displayName,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.padding(start = 8.dp)
    )
  }
}
