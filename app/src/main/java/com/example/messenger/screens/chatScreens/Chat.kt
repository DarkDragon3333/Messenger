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
import com.google.firebase.database.ValueEventListener
import java.net.URLDecoder

private lateinit var refToMessages: DatabaseReference
private lateinit var MessagesListener: ValueEventListener

@Composable
fun ChatScreen(
    fullnameContact: String?,
    statusContact: String?,
    photoURLContact: String?,
    idContact: String?,
    navController: NavHostController, ) {
    val fullname = URLDecoder.decode(fullnameContact, "UTF-8")
    val statusUSER = URLDecoder.decode(statusContact, "UTF-8")
    val photoURL = photoURLContact
    val id = URLDecoder.decode(idContact, "UTF-8")

    val a = remember { mutableStateListOf<CommonModal>() }

    var text by remember { mutableStateOf("") }

    fun initChat(id: String) {
        refToMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)

        MessagesListener = AppValueEventListener { dataSnap ->
            cacheMessages =
                dataSnap.children.map { it.getCommonModel() }.toMutableList()
            a.clear()
            cacheMessages.forEach { message ->
                a.add(message)
            }
        }

        refToMessages.addValueEventListener(MessagesListener)
    }

    if (id != null) {
        initChat(id)
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f),
            contentAlignment = Alignment.TopStart
        )
        {

            if (a.size > 0) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(a.size) { index ->
                        Message(a[index])
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 10.dp),
            contentAlignment = Alignment.BottomCenter) {
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
                                sendMessage(message, id, TYPE_TEXT) {
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
    navController.addOnDestinationChangedListener{ _, destination, _ ->
        if (destination.route != "chatScreen/{fullname}/{status}/{photoURL}/{id}"){
            refToMessages.removeEventListener(MessagesListener)
        }
    }
}


