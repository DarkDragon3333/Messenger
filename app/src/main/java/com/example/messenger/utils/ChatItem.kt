package com.example.messenger.utils

import androidx.compose.runtime.Stable
import com.google.firebase.Timestamp

@Stable
interface ChatItem {
    val id: Any
    val type: String
    var status: String
    val lastMessage: String?
    val timeStamp: Timestamp?
}