package com.thinkcloud.llmclient.ui.config.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SaveButton(
    isSaving: Boolean,
    isSuccess: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else if (isSuccess) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "保存成功",
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "保存",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = when {
                        isSaving -> "保存中..."
                        isSuccess -> "保存成功"
                        else -> "保存配置"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}