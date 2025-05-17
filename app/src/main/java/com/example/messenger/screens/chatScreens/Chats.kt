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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.UID
import com.example.messenger.dataBase.initChatsList
import com.example.messenger.dataBase.listeningUpdateChatsList
import com.example.messenger.modals.ChatModal
import com.example.messenger.screens.componentOfScreens.ElementOfChatsList
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

@Composable
fun ChatsScreen(navController: NavHostController) {
    var listenerRegistration: ListenerRegistration

    val listState = rememberLazyListState()
    val chatsScreenState = remember { mutableStateListOf<ChatModal>() }

    val isLoadingFirstChats = remember { mutableStateOf(false) }
    var isLoadingOldChats by remember { mutableStateOf(false) }

    val db = Firebase.firestore

    val messLink =
        db
            .collection("users_talkers").document(UID)
            .collection("talkers")


    if (isLoadingFirstChats.value)
        ChatsList(chatsScreenState, listState, messLink, navController)

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.any { it.index == chatsScreenState.lastIndex - 10 }
        }.collect { isVisible ->
//            if (isVisible && !isLoadingOldMessages && chatsScreenState.isNotEmpty()) {
//                isLoadingOldMessages = true
//
//                val lastTimestamp = chatsScreenState[chatsScreenState.lastIndex].timeStamp
//                messLink
//                    .startAfter(lastTimestamp)
//                    .limit(30)
//                    .get()
//                    .addOnSuccessListener { result ->
//                        val newMessages = result.documents.mapNotNull {
//                            it.toObject(ChatModal::class.java)
//                        }.filterNot { msg ->
//                            chatsScreenState.any { it.id == msg.id }
//                        }
//
//                        chatsScreenState.addAll(newMessages)
//
//                        isLoadingOldMessages = false
//                    }
//                    .addOnFailureListener {
//                        isLoadingOldMessages = false
//                    }
//            }
        }
    }

    DisposableEffect(Unit) {
        initChatsList(chatsScreenState, messLink) { isLoadingFirstChats.value = true }
        listenerRegistration = listeningUpdateChatsList(chatsScreenState, messLink)

        onDispose {
            chatsScreenState.clear()
            listenerRegistration.remove()
        }
    }
}

@Composable
private fun ChatsList(
    chatsScreenState: SnapshotStateList<ChatModal>,
    listState: LazyListState,
    messLink: Query,
    navController: NavHostController
) {
    if (chatsScreenState.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
        ) {
            items(chatsScreenState, key = { it.id }) { chat ->
                ElementOfChatsList(chat, navController)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    }
}

