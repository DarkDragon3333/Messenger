package com.example.messenger.dataBase.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushService : FirebaseMessagingService(){

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        //Сделать сохранение токена в БД
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        //Сделать сохранение токена в БД
        //Здесь отвечать на полученное уведомление

    }
}