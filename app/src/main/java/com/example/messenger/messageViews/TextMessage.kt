package com.example.messenger.messageViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.dataBase.sendMessage
import com.example.messenger.modals.MessageModal
import com.example.messenger.ui.theme.textMes
import com.example.messenger.utilsFilies.Constants.TYPE_TEXT

@Composable
fun TextMsg(message: Pair<MessageModal, Any>) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Row(
            modifier = Modifier
                .background(textMes)
                .padding(8.dp)
        )
        {
            Text(
                text = message.first.info,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 60.dp)
            )

        }
        Text(
            text = message.second.toString(),
            fontSize = 10.sp,
            modifier = Modifier
                .background(textMes)
                .padding(end = 6.dp, bottom = 2.dp)
                .align(Alignment.BottomEnd)
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