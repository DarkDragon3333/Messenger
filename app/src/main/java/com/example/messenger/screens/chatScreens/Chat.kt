package com.example.messenger.screens.chatScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.NODE_MESSAGES
import com.example.messenger.dataBase.REF_DATABASE_ROOT
import com.example.messenger.dataBase.TYPE_TEXT
import com.example.messenger.dataBase.UID
import com.example.messenger.dataBase.sendMessage
import com.example.messenger.dataBase.valueEventListenerClasses.AppValueEventListener
import com.example.messenger.modals.CommonModal
import com.example.messenger.utilsFilies.cacheMessages
import com.example.messenger.utilsFilies.getCommonModel
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.launch
import java.net.URLDecoder

private lateinit var refToMessages: DatabaseReference
private lateinit var MessagesListener: AppValueEventListener

@Composable
fun ChatScreen(
    fullnameContact: String?,
    statusContact: String?,
    photoURLContact: String?,
    idContact: String?,
    navController: NavHostController,
) {
    val fullname = URLDecoder.decode(fullnameContact, "UTF-8")
    val statusUSER = URLDecoder.decode(statusContact, "UTF-8")
    val photoURL = photoURLContact
    val id = idContact
    val chatScreenState = remember { mutableStateListOf<CommonModal>() }

    var text by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val regex = Regex("[{}]")
    val result = id.toString().replace(regex, "")

    /*var countOfMessage = 10*/


    fun initChat(id: String/*, count: Int*/) {
        refToMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)
        MessagesListener = AppValueEventListener { dataSnap ->
            cacheMessages = dataSnap.children.map { it.getCommonModel() }.toMutableList()
            if (chatScreenState.isNotEmpty()) {
                if (
                    (cacheMessages.last().timeStamp.toString().toLong() / 1000) !=
                    (chatScreenState.last().timeStamp.toString().toLong() / 1000)
                    ) {
                    chatScreenState.add(cacheMessages.last())
                    cacheMessages.clear()
                    coroutineScope.launch() {
                        listState.animateScrollToItem(chatScreenState.lastIndex)
                    }
                }
            } else {
                chatScreenState.addAll(cacheMessages)
                cacheMessages.clear()
            }

        }

        refToMessages.addValueEventListener(MessagesListener)

    }



    if (id != null) {
        initChat(result/*, countOfMessage*/)
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.getDistance() > 50) {
                    /*countOfMessage += 10
                    refToMessages.removeEventListener(MessagesListener)
                    initChat(result, countOfMessage)*/
                }
                return Offset.Zero
            }

        }
    }

    /*val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                extracted()
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                extracted()
                return super.onPostFling(consumed, available)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                extracted()
                return super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                extracted()
                return super.onPreFling(available)
            }

            private fun extracted() {
                count += 10
                refToMessages.limitToLast(count).addValueEventListener(MessagesListener)
            }
        }
    }*/

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            contentAlignment = Alignment.TopStart
        )
        {
            if (chatScreenState.size > 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize().nestedScroll(nestedScrollConnection)
                    /*.nestedScroll(nestedScrollConnection)*/,
                    state = listState
                ) {
                    items(chatScreenState.size) { index ->
                        Message(chatScreenState[index])
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    coroutineScope.launch {
                        listState.animateScrollToItem(chatScreenState.lastIndex)
                    }

                }

            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(65.dp),
                placeholder = { Text(text = "Введите сообщение") },
                trailingIcon = {
                    IconButton(onClick = {
                        val message = text
                        if (message.isEmpty()) {
                            makeToast("Введите сообщение", mainActivityContext)
                        } else {
                            sendMessage(message, result, TYPE_TEXT) {
                                text = ""
                                coroutineScope.launch() {
                                    listState.animateScrollToItem(chatScreenState.lastIndex)
                                }
                            }
                        }
                    }) {
                        Column {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "")
                        }

                    }
                },
            )
        }

    }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        if (destination.route != "chatScreen/{fullname}/{status}/{photoURL}/{id}") {
            refToMessages.removeEventListener(MessagesListener)
        }
    }
}

private fun isEditTagItemFullyVisible(
    lazyListState: LazyListState,
    editTagItemIndex: Int
): Boolean {
    with(lazyListState.layoutInfo) {
        val editingTagItemVisibleInfo = visibleItemsInfo.find { it.index == editTagItemIndex }
        return if (editingTagItemVisibleInfo == null) {
            false
        } else {
            viewportEndOffset - editingTagItemVisibleInfo.offset >= editingTagItemVisibleInfo.size
        }
    }
}
