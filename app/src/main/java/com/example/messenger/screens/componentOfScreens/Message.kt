package com.example.messenger.screens.componentOfScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.messageViews.CreateMessageView
import com.example.messenger.modals.MessageModal
import com.example.messenger.modals.User
import com.example.messenger.utils.Constants.TYPE_GROUP
import com.example.messenger.utils.UriImage
import com.example.messenger.viewModals.GroupChatViewModal

@Composable
fun Message(
    messageModal: MessageModal,
    typeChat: String = "",
    showAvatar: Boolean = false,
    avatarUrl: String = "",
) {
    Row(modifier = Modifier.fillMaxSize()) {
        if (messageModal.from != UID) {
            Row(horizontalArrangement = Arrangement.Start)
            {
                when (typeChat == TYPE_GROUP && showAvatar) {
                    true -> {
                        Spacer(modifier = Modifier.width(10.dp))
                        UriImage(40.dp, avatarUrl) { }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    else -> Spacer(modifier = Modifier.width(60.dp))
                }

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
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    }
}


@Composable
fun OutlinedCardMessage(
    messageModal: MessageModal,
    arrangement: Arrangement.Horizontal,
){
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

