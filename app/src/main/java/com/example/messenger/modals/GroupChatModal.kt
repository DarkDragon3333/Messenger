package com.example.messenger.modals

import androidx.compose.runtime.Stable
import com.example.messenger.utils.ChatItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.io.Serializable

@Stable
data class GroupChatModal(
    var groupChatName: String = "",
    var photoUrl: String = "",
    override var id: String = " ",
    override var status: String = "",
    var contactList: MutableList<String> = mutableListOf(),
    override var type: String = " ",
    override var lastMessage: String? = "empty",
    override val timeStamp: Timestamp? = null,
) : ChatItem, Serializable
