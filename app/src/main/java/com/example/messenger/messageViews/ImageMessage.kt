package com.example.messenger.messageViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.modals.MessageModal
import com.example.messenger.utils.MessageImage

@Composable
fun ImageMsg(pair: Pair<MessageModal, Any>) {
    Box {
        MessageImage(uri = pair.first.info) // предполагается, ты используешь AsyncImage внутри
        Text(
            text = pair.second.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 2.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}