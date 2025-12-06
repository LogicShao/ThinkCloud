package com.thinkcloud.llmclient.ui.chat.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography

/**
 * Markdown 内容渲染组件
 *
 * @param content Markdown 文本内容
 * @param textColor 文本颜色
 * @param modifier 修饰符
 */
@Composable
fun MarkdownContent(
    content: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography

    Markdown(
        content = content,
        modifier = modifier.padding(top = 4.dp),
        // 自定义颜色
        colors = markdownColor(
            text = textColor
        ),
        // 自定义排版
        typography = markdownTypography(
            h1 = typography.headlineLarge.copy(color = textColor),
            h2 = typography.headlineMedium.copy(color = textColor),
            h3 = typography.headlineSmall.copy(color = textColor),
            h4 = typography.titleLarge.copy(color = textColor),
            h5 = typography.titleMedium.copy(color = textColor),
            h6 = typography.titleSmall.copy(color = textColor),
            text = typography.bodyMedium.copy(color = textColor),
            code = typography.bodySmall.copy(color = textColor),
            quote = typography.bodyMedium.copy(color = textColor),
            paragraph = typography.bodyMedium.copy(color = textColor),
            ordered = typography.bodyMedium.copy(color = textColor),
            bullet = typography.bodyMedium.copy(color = textColor),
            list = typography.bodyMedium.copy(color = textColor)
        )
    )
}
