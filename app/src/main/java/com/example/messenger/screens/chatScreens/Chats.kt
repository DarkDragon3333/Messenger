package com.example.messenger.screens.chatScreens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.messenger.screens.componentOfScreens.ElementOfChatsList

@Composable
fun ChatsScreen(navController: NavHostController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {ElementOfChatsList(navController)}
    }
}

