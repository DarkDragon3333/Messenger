package com.example.messenger.modals

import androidx.compose.runtime.Stable
import com.example.messenger.utils.ChatItem
import com.google.firebase.Timestamp
@Stable
data class ChatModal(
    var fullname: String = " ",
    var photoUrl: String = " ",
    override var id: String = " ",
    override var status: String = " ",
    override var type: String = " ",
    override var lastMessage: String? = "empty",
    override val timeStamp: Timestamp? = null,
) : ChatItem
//TO-DO разобраться с timeStamp: TimeStamp
