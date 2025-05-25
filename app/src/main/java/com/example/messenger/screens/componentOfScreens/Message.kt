package com.example.messenger.screens.componentOfScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.messageViews.CreateMessageView
import com.example.messenger.modals.MessageModal

@Composable
fun Message(messageModal: MessageModal) {
    Row(modifier = Modifier.fillMaxSize()) {
        if (messageModal.from != UID) {
            Row(horizontalArrangement = Arrangement.Start)
            {
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedCardMessage(messageModal, Arrangement.Start)
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            )
            {
                OutlinedCardMessage(messageModal, Arrangement.End)
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

    }
}


@Composable
fun OutlinedCardMessage(
    messageModal: MessageModal,
    arrangement: Arrangement.Horizontal,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f),
        horizontalArrangement = arrangement
    ) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(1.5.dp, Color.Black),
        )
        {
            CreateMessageView(messageModal)
        }
    }
}

