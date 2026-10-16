package com.example.messenger.messageViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.dataBase.firebaseFuns.sendMessage
import com.example.messenger.dataBase.firebaseFuns.sendMessageToGroupChat
import com.example.messenger.modals.MessageModal
import com.example.messenger.ui.theme.textMes
import com.example.messenger.utils.Constants.TYPE_TEXT
import kotlin.collections.mutableListOf

@Composable
fun TextMsg(message: Pair<MessageModal, Any>) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp)
    ) {
        Text(
            text = message.first.info,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 48.dp)
        )

        Text(
            text = message.second.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 2.dp)
        )
    }
}

fun sendText(
    text: String,
    receivingUserID: String,
): String {
    var tempText = text
    sendMessage(
        info = tempText.trim(),
        receivingUserID = receivingUserID,
        typeMessage = TYPE_TEXT,
        key = ""
    ) {
        tempText = ""
    }

    return tempText
}

fun sendTextToGroupChat(
    text: String,
    groupChatId: String,
    contactListId: MutableList<String>,
): String {
    var tempText = text
    sendMessageToGroupChat (
        info = tempText.trim(),
        groupChatId = groupChatId,
        contactListId = contactListId,
        typeMessage = TYPE_TEXT,
        key = ""
    ) {
        tempText = ""
    }

    return tempText
}