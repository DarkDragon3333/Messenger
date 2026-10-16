package com.example.messenger.modals

import androidx.compose.runtime.Stable
import com.example.messenger.modals.ChatItem
import com.google.firebase.Timestamp
@Stable
data class ChatModal(
    override var chatName: String = " ",
    override var photoUrl: String = " ",
    override var id: String = " ",
    override var status: String = " ",
    override var type: String = " ",
    override var lastMessage: String? = " ",
    override val timeStamp: Timestamp? = null,
) : ChatItem
//TO-DO разобраться с timeStamp: TimeStamp
