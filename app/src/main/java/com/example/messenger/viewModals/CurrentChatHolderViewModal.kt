package com.example.messenger.viewModals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.messenger.modals.ChatModal
import com.example.messenger.modals.GroupChatModal

class CurrentChatHolderViewModal : ViewModel(){

    var currentChat by mutableStateOf<ChatModal?>(null)
        private set

    var currentGroupChat by mutableStateOf<GroupChatModal?>(null)
        private set

    fun setChat(chat: ChatModal) {
        currentChat = chat
        currentGroupChat = null
    }

    fun setGroupChat(groupChat: GroupChatModal) {
        currentGroupChat = groupChat
        currentChat = null
    }

    fun clearChat() {
        currentChat = null
        currentGroupChat = null
    }
}