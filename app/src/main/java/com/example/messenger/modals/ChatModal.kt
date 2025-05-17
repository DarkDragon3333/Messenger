package com.example.messenger.modals

import com.google.firebase.Timestamp

data class ChatModal(
    var fullname: String = " ",
    var photoUrl: String = "",
    var id: String = " ",
    var status: String = "",
    var type: String = " ",
    var lastMessage: String? = "empty",
    val timeStamp: String? = null,
)
//TO-DO разобраться с timeStamp: TimeStamp
