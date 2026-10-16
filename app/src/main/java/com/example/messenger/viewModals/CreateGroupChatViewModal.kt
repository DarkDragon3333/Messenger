package com.example.messenger.viewModals

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.messenger.modals.ChatItem

class CreateGroupChatViewModal : ViewModel() {
    private val _contactsList = mutableStateListOf<ChatItem>()
    val contactsList: SnapshotStateList<ChatItem> get() = _contactsList

    fun addContact(){

    }
}