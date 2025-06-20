package com.example.messenger.screens.componentOfScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.messenger.modals.ChatModal
import com.example.messenger.modals.GroupChatModal
import com.example.messenger.navigation.Screens
import com.example.messenger.modals.ChatItem
import com.example.messenger.utils.UriImage
import com.example.messenger.utils.goTo
import com.example.messenger.viewModals.CurrentChatHolderViewModal

@Composable
fun ElementOfChatsList(
    chatType: ChatItem,
    navController: NavHostController,
    currentChatHolderViewModal: CurrentChatHolderViewModal = viewModel()
) {
    val isGroup = chatType is GroupChatModal
    val title = chatType.chatName
    val subtitle = chatType.lastMessage?.take(30)?.let { if (it.length == 30) "$it..." else it }
    val trailingText = if (isGroup) "Группа" else (chatType as ChatModal).status
    val photoUrl = chatType.photoUrl

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding( vertical = 2.dp) // уменьшенные отступы
            .clickable {
                if (isGroup) {
                    currentChatHolderViewModal.setGroupChat(chatType as GroupChatModal)
                    goTo(navController, Screens.GroupChat)
                } else {
                    currentChatHolderViewModal.setChat(chatType as ChatModal)
                    goTo(navController, Screens.Chat)
                }
            },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UriImage(48.dp,  photoUrl) {} // чуть меньше аватар

            Spacer(modifier = Modifier.width(8.dp)) // уменьшен промежуток

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 6.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = trailingText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


