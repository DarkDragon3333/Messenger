package com.example.messenger.screens.chatScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.modals.ContactModal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun GroupChat(
    navController: NavHostController,
    contactsList: MutableList<ContactModal>?,
    groupChatName: String,
    photoUrlGroupChat: String
){
    val messages = remember { mutableStateOf(listOf(contactsList)) }

    val chatScreenState by remember {
        derivedStateOf { messages.value }
    }

    val listState = rememberLazyListState()

    Column {
        Text(groupChatName)
        Text(photoUrlGroupChat)
        LazyColumn (
            modifier = Modifier,
            state = listState,
        )
        {
            items(chatScreenState) { message ->
                Text(message.toString())
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}