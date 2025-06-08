package com.example.messenger.modals

import androidx.compose.runtime.Stable
import com.example.messenger.utils.ChatItem

@Stable
data class GroupChatModal(
    var groupChatName: String = "",
    var photoUrl: String = "",
    override var id: String = " ",
    var contactList: MutableList<String> = mutableListOf(),
    var type: String = " ",
    var lastMessage: String? = "empty",
    val timeStamp: String? = null,
) : ChatItem
