package com.example.messenger.screens.chatScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.modals.CommonModal
import com.example.messenger.utilsFilies.UID
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
                Row (horizontalArrangement = Arrangement.Start)
                {
                    Spacer(modifier = Modifier.width(15.dp))
                    Row(modifier = Modifier
                        .background(Color.Green)
                        .border(
                            border = BorderStroke(1.dp, Color.Black),
                            shape = RoundedCornerShape(4.dp)
                        ),
                        horizontalArrangement = Arrangement.Start)
                    {
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.Start)
                        {
                            Text(text = message)
                            Text(text = timeStamp, fontSize = 10.sp, textAlign = TextAlign.Start)
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                    }
                }

            }
            else {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                    .fillMaxWidth())
                {
                    Row(horizontalArrangement = Arrangement.End)
                    {
                        Row(modifier = Modifier
                            .background(Color.Green)
                            .border(
                                border = BorderStroke(1.dp, Color.Black),
                                shape = RoundedCornerShape(4.dp)
                            ),
                            horizontalArrangement = Arrangement.End)
                        {
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = message)
                                Text(text = timeStamp, fontSize = 10.sp, textAlign = TextAlign.End)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                    }
                    Spacer(modifier = Modifier.width(5.dp))
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

fun String.asTimestamp() : String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}