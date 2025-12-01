package com.thinkcloud.llmclient.ui.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkcloud.llmclient.domain.repository.ConversationRepository
import com.thinkcloud.llmclient.ui.conversation.state.ConversationListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 对话列表 ViewModel
 */
class ConversationListViewModel(
  private val conversationRepository: ConversationRepository
) : ViewModel() {

  private val _state = MutableStateFlow(ConversationListState())
  val state: StateFlow<ConversationListState> = _state.asStateFlow()

  init {
    loadConversations()
  }

  /**
   * 加载对话列表
   */
  private fun loadConversations() {
    viewModelScope.launch {
      _state.update { it.copy(isLoading = true) }

      conversationRepository.getAllConversations()
        .catch { e ->
          _state.update {
            it.copy(
              isLoading = false,
              errorMessage = "加载对话历史失败: ${e.message}"
            )
          }
        }
        .collect { conversations ->
          _state.update {
            it.copy(
              conversations = conversations,
              isLoading = false,
              errorMessage = null
            )
          }
        }
    }
  }

  /**
   * 删除对话
   */
  fun deleteConversation(conversationId: String) {
    viewModelScope.launch {
      try {
        conversationRepository.deleteConversation(conversationId)
      } catch (e: Exception) {
        _state.update {
          it.copy(errorMessage = "删除对话失败: ${e.message}")
        }
      }
    }
  }

  /**
   * 清空错误消息
   */
  fun clearError() {
    _state.update { it.copy(errorMessage = null) }
  }
}
