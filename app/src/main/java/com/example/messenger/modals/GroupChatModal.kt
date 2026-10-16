package com.example.messenger.modals

import androidx.compose.runtime.Stable
import com.example.messenger.modals.ChatItem
import com.google.firebase.Timestamp
import java.io.Serializable

@Stable
data class GroupChatModal(
    override var chatName: String = "",
    override var photoUrl: String = "",
    override var id: String = " ",
    override var status: String = "",
    var contactList: MutableList<String> = mutableListOf(),
    var administrator: String = "",
    override var type: String = " ",
    override var lastMessage: String? = "empty",
    override val timeStamp: Timestamp? = null,
) : ChatItem, Serializable
