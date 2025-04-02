package com.example.messenger.modals

import androidx.compose.runtime.Stable

@Stable
data class MessageModal (
    var from: String = " ",
    var id: String = " ",
    var info: String = "empty",
    var timeStamp: Any = " ",
    var type: String = " ",
)