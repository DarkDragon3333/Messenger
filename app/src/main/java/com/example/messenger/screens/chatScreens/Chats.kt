package com.example.messenger.screens.chatScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.screens.componentOfScreens.ElementOfChatsList
import com.example.messenger.modals.ChatItem
import com.example.messenger.viewModals.ChatsViewModal
import com.example.messenger.viewModals.CurrentChatHolderViewModal

@Composable
fun ChatsScreen(
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal = viewModel(),
    chatsViewModal: ChatsViewModal = viewModel()
) {
    val listState = rememberLazyListState()

    if (chatsViewModal.getFlagDownloadFirstChats())
        ChatsList(chatsViewModal.chatsList, listState, navController, currentChatViewModel)

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.any { it.index == chatsViewModal.chatsList.lastIndex - 10 }
        }.collect { isVisible ->
            if (isVisible) chatsViewModal.downloadOldChats(UID)
        }
    }

    DisposableEffect(Unit) {
        chatsViewModal.initChatsList(UID) { chatsViewModal.setFlagDownloadFirstChats(true) }
        chatsViewModal.updateChatsListData()
        chatsViewModal.startListingChatsList(UID)
        chatsViewModal.listingUsersData()
        chatsViewModal.listingGroupChatData()

        onDispose {
            chatsViewModal.removeListener()
        }
    }
}

@Composable
private fun ChatsList(
    chatsScreenState: List<ChatItem>,
    listState: LazyListState,
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal,
    modifier: Modifier = Modifier
) {
    if (chatsScreenState.isNotEmpty()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier.fillMaxSize().padding(horizontal = 8.dp)
        ) {
            items(
                items = chatsScreenState,
                key = { it.id.toString() + "_" + (it.lastMessage ?: "") + "_" + (it.timeStamp
                    ?: "") + "_" + it.status + "_" + it.photoUrl + "_" + it.chatName }
            ) { chat ->
                ElementOfChatsList(chat, navController, currentChatViewModel)
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Список чатов пуст",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

