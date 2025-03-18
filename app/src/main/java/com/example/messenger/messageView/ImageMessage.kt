package com.example.messenger.messageView

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.modals.MessageModal
import com.example.messenger.utilsFilies.MessageImage

@Composable
fun ImageMsg(
    messageModal: MessageModal,
    timeStamp: String
) {
    Box {
        MessageImage(uri = messageModal.info)
        Text(
            text = timeStamp,
            fontSize = 10.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 6.dp, bottom = 2.dp)
        )
    }
}