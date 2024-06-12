package com.example.messenger.screens.chatScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.modals.CommonModal
import com.example.messenger.ui.theme.textMes
import com.example.messenger.dataBase.UID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Message(commonModal: CommonModal) {
    val id = commonModal.from
    Row(modifier = Modifier.fillMaxSize())
    {
        if (id != "" && id != " ") {
            val pair = initMessage(commonModal)
            val message = pair.first
            val timeStamp = pair.second.toString()

            if (id != UID) {
                Row(horizontalArrangement = Arrangement.Start)
                {
                    Spacer(modifier = Modifier.width(15.dp))
                    OutlinedCardMessage(message, timeStamp, 1)
                }
            } else {
                Row(horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    OutlinedCardMessage(message, timeStamp, 0)
                }

            }
        }


    }

}

@Composable
private fun initMessage(commonModal: CommonModal): Pair<String, Any> {
    val message = commonModal.text
    var timeStamp = commonModal.timeStamp
    if (timeStamp != "" && timeStamp != " ") {
        timeStamp = commonModal.timeStamp.toString().asTimestamp()
    }
    return Pair(message, timeStamp)
}

fun String.asTimestamp(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

@Composable
fun OutlinedCardMessage(message: String, timeStamp: String, flag: Int) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
    ) {
        if (flag == 0) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .background(textMes)
                    .padding(8.dp)
            )
            {
                Text(text = message)
                Text(text = timeStamp, fontSize = 10.sp, textAlign = TextAlign.End)
            }
        } else {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .background(textMes)
                    .padding(8.dp)
            ) {
                Text(text = message)
                Text(text = timeStamp, fontSize = 10.sp, textAlign = TextAlign.Start)
            }
        }

    }
}