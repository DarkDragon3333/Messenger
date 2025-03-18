package com.example.messenger.messageView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.dataBase.TYPE_TEXT
import com.example.messenger.dataBase.sendMessage
import com.example.messenger.ui.theme.textMes

@Composable
fun TextMsg(pair: Pair<String, Any>) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Row(
            modifier = Modifier
                .background(textMes)
                .padding(8.dp)
        )
        {
            androidx.compose.material3.Text(
                text = pair.first,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 60.dp)
            )

        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .background(textMes)
        ) {
            androidx.compose.material3.Text(
                text = pair.second.toString(),
                fontSize = 10.sp,
                modifier = Modifier
                    .padding(end = 6.dp, bottom = 2.dp)
            )
        }
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