package com.example.messenger.screens.chatScreens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.screens.componentOfScreens.ElementOfChatsList
import com.example.messenger.utils.ChatItem
import com.example.messenger.viewModals.ChatsViewModal
import com.example.messenger.viewModals.CurrentChatHolderViewModal

@Composable
fun ChatsScreen(
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal = viewModel()
) {
    val chatsViewModal: ChatsViewModal = viewModel()
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
        chatsViewModal.startListingChatsList(UID)
        chatsViewModal.listingUsersStatus()

        onDispose {
            chatsViewModal.removeListener()
        }
    }
}

@Composable
private fun ChatsList(
    chatsScreenState: MutableList<ChatItem>,
    listState: LazyListState,
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal = viewModel()
) {
    if (chatsScreenState.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
        ) {
            items(
                chatsScreenState,
                key = {
                    it.id.toString() + "_" + (it.lastMessage ?: "") + "_" + (it.timeStamp
                        ?: "") + "_" + it.status
                }) { chat ->
                ElementOfChatsList(chat, navController, currentChatViewModel)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    }
}

