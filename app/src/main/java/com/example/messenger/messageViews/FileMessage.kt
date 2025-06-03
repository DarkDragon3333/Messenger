package com.example.messenger.messageViews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.R
import com.example.messenger.modals.MessageModal
import com.example.messenger.ui.theme.textMes
import com.example.messenger.utils.parseInfo

@Composable
fun FileMsg(
    pair: Pair<MessageModal, Any>
) {
    var (fileName, fileUri) = remember { parseInfo(pair.first.info) }
    Box(contentAlignment = Alignment.BottomEnd) {
        Row(
            modifier = Modifier
                .background(textMes)
                .padding(8.dp)
        )
        {
            IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
                    .background(Color.White),
                onClick = {
                    fileUri = parseInfo(pair.first.info).second
                    downloadFileToSmartphone(fileUri)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_insert_drive_file_24),
                    contentDescription = null
                )
            }
            Text(
                text = fileName,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 60.dp)
            )
        }

        Text(
            text = pair.second.toString(),
            fontSize = 10.sp,
            modifier = Modifier
                .background(textMes)
                .padding(end = 6.dp, bottom = 2.dp)
                .align(Alignment.BottomEnd)
        )
    }

    DisposableEffect(Unit) {
        //fileName = parseInfo(pair.first.info).first

        onDispose {

        }
    }
}

fun downloadFileToSmartphone(string: String) {

}