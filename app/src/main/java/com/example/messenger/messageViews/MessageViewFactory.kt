package com.example.messenger.messageViews

import androidx.compose.runtime.Composable
import com.example.messenger.modals.MessageModal
import com.example.messenger.utilsFilies.Constants.TYPE_IMAGE
import com.example.messenger.utilsFilies.Constants.TYPE_TEXT
import com.example.messenger.utilsFilies.Constants.TYPE_VOICE
import com.example.messenger.utilsFilies.toFormattedLocalTime

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
        Pair(messageModal, messageModal.timeStamp.toFormattedLocalTime())
    else
        Pair(messageModal, "Error")
}

