package com.example.messenger.modals

import androidx.compose.runtime.Stable
import com.example.messenger.utils.ChatItem
import com.google.firebase.Timestamp
@Stable
data class ChatModal(
    var fullname: String = " ",
    var photoUrl: String = "",
    override var id: String = " ",
    var status: String = "",
    override var type: String = " ",
    var lastMessage: String? = "empty",
    val timeStamp: String? = null,
) : ChatItem
//TO-DO разобраться с timeStamp: TimeStamp
