package com.example.messenger.modals

import androidx.compose.runtime.Stable
import com.google.firebase.Timestamp

@Stable
interface ChatItem {
    val chatName: String
    val photoUrl: String
    val id: Any
    val type: String
    var status: String
    val lastMessage: String?
    val timeStamp: Timestamp?
}