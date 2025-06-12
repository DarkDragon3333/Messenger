package com.example.messenger.viewModals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.messenger.modals.ChatModal

class GroupChatViewModal : ViewModel() {
    // Внутренняя изменяемая карта (реактивная)
    private val _mapContactIdToPhotoUrl = mutableStateMapOf<String, Any>()
    private val _lastMessageId = mutableStateOf<String>("")

    // Публичный геттер — доступ только на чтение
    val mapContactIdToPhotoUrl: Map<String, Any>
        get() = _mapContactIdToPhotoUrl

    fun getLastMessageId(): String{
        return _lastMessageId.value.toString()
    }

    fun setLastMessageId(newMessageId: String){
        _lastMessageId.value = newMessageId
    }

    // Установка или обновление URL по ID контакта
    fun setPhotoUrl(contactId: String, photoUrl: Any) {
        _mapContactIdToPhotoUrl[contactId] = photoUrl
    }

    // Получение URL по ID (если нужно отдельно)
    fun getPhotoUrl(contactId: String): Any? {
        return _mapContactIdToPhotoUrl[contactId]
    }

}