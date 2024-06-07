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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.modals.CommonModal
import com.example.messenger.utilsFilies.AppValueEventListener
import com.example.messenger.utilsFilies.NODE_MESSAGES
import com.example.messenger.utilsFilies.REF_DATABASE_ROOT
import com.example.messenger.utilsFilies.TYPE_TEXT
import com.example.messenger.utilsFilies.UID
import com.example.messenger.utilsFilies.cacheMessages
import com.example.messenger.utilsFilies.getCommonModel
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.sendMessage
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

    //var count = 10

    fun initChat(id: String) {
        refToMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)

        MessagesListener = AppValueEventListener { dataSnap ->
            cacheMessages = dataSnap.children.map { it.getCommonModel() }.toMutableList()
            if (chatScreenState.isNotEmpty()){
                if (cacheMessages.last().timeStamp != chatScreenState.last().timeStamp) {
                    chatScreenState.clear()
                    chatScreenState.addAll(cacheMessages)
                }
            }
             else{
                chatScreenState.addAll(cacheMessages)
            }

        }

        refToMessages.addValueEventListener(MessagesListener)

    }

    if (id != null) {
        initChat(result)
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
                .fillMaxHeight(0.75f),
            contentAlignment = Alignment.TopStart
        )
        {

            if (chatScreenState.size > 0) {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    /*.nestedScroll(nestedScrollConnection)*/,
                    state = listState
                ) {
                    items(chatScreenState.size,) { index ->
                        Message(chatScreenState[index])
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    coroutineScope.launch() {
                        listState.animateScrollToItem(chatScreenState.lastIndex)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 10.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Введите сообщение") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val message = text
                            if (message.isEmpty()) {
                                makeToast("Введите сообщение", mainActivityContext)
                            } else {

                                sendMessage(message, result, TYPE_TEXT) {
                                    text = ""
                                }
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "")
                        }
                    }
                )
            }
        }

    }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        if (destination.route != "chatScreen/{fullname}/{status}/{photoURL}/{id}") {
            refToMessages.removeEventListener(MessagesListener)
        }
    }
}


