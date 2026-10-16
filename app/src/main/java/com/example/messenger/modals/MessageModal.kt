package com.example.messenger.modals

import androidx.compose.runtime.Stable
import com.google.firebase.Timestamp

@Stable
data class MessageModal (
    var from: String = " ",
    var id: String = " ",
    var info: String = "empty",
    val timeStamp: Timestamp? = null,
    var type: String = " ",
)