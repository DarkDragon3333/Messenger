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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messenger.utilsFilies.TYPE_TEXT
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.sendMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(user: String?, photoURL: String?, id: String?, id1: String?) {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.90f), contentAlignment = Alignment.TopStart) {
            Message()
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 10.dp), contentAlignment = Alignment.BottomCenter) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = text,
                    onValueChange = {text = it},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {Text(text = "Введите сообщение")},
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

}


