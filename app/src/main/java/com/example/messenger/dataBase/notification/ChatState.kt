package com.example.messenger.dataBase.notification

data class ChatState(
    val isEnteringToken: Boolean = true,
    val remoteToken: String = " ",
    val messageText: String = " "
)
