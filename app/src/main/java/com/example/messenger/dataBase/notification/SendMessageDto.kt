package com.example.messenger.dataBase.notification

data class SendMessageDto( //Объект передачи данных
    val to: String?,
    val notification: NotificationBody
)

data class NotificationBody( //Тело уведомления
    val title : String,
    val body: String
)
