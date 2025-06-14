package com.example.messenger.utils

import androidx.compose.runtime.Stable

@Stable
interface ChatItem {
    val id: Any
    val type: String
    var status: String
    val lastMessage: String?
    val timeStamp: String?
}