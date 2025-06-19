package com.example.messenger.dataBase.notification

import retrofit2.http.Body
import retrofit2.http.POST

interface FmcApi { //API который определяет запросы отправляемые на сервер

    @POST("/send")
    suspend fun sendMessage(
        @Body body: SendMessageDto
    )

    @POST("/broadcast")
    suspend fun broadcast(
        @Body body: SendMessageDto
    )

}