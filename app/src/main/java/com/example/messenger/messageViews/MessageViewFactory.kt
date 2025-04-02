package com.example.messenger.messageViews

import androidx.compose.runtime.Composable
import com.example.messenger.dataBase.TYPE_IMAGE
import com.example.messenger.dataBase.TYPE_TEXT
import com.example.messenger.dataBase.TYPE_VOICE
import com.example.messenger.modals.MessageModal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageViewFactory {
    @Composable
    fun CreateMessageView(messageFromDB: MessageModal){
        val message = initMessage(messageFromDB)

         when (messageFromDB.type) {
            TYPE_TEXT -> TextMsg(message)

            TYPE_IMAGE -> ImageMsg(message)

            TYPE_VOICE -> VoiceMsg(message)

            else -> TextMsg(message)
        }
    }
}


fun initMessage(messageModal: MessageModal): Pair<MessageModal, Any> {
    return if (messageModal.timeStamp.toString().trim().isNotEmpty())
        Pair(messageModal, messageModal.timeStamp.toString().asTimestamp())
    else
        Pair(messageModal, "Error")

}

fun String.asTimestamp(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}