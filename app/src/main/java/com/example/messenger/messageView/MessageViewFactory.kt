package com.example.messenger.messageView

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.TYPE_IMAGE
import com.example.messenger.dataBase.TYPE_TEXT
import com.example.messenger.dataBase.TYPE_VOICE
import com.example.messenger.modals.MessageModal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageViewFactory {
    @Composable
    fun CreateMessageView(messageModal: MessageModal, navController: NavHostController){
        val pair = initMessage(messageModal)
        val timeStamp = pair.second.toString()

        return when (messageModal.type) {
            TYPE_TEXT -> TextMessage(pair)

            TYPE_IMAGE -> ImageMessage(messageModal, timeStamp)

            TYPE_VOICE -> VoiceMessage(messageModal, timeStamp, navController)
            else -> TextMessage(pair)
        }
    }
}

@Composable
fun initMessage(messageModal: MessageModal): Pair<String, Any> {
    val message = messageModal.info
    var timeStamp = messageModal.timeStamp
    if (timeStamp != "" && timeStamp != " ") {
        timeStamp = messageModal.timeStamp.toString().asTimestamp()
    }
    return Pair(message, timeStamp)
}

fun String.asTimestamp(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}