package com.example.messenger.screens.chatScreens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.screens.componentOfScreens.ElementOfChatsList

@Composable
fun ChatsScreen(navController: NavHostController) {
    Spacer(modifier = Modifier.height(8.dp))
    LazyColumn(modifier = Modifier.fillMaxSize()) {
    item {
            Spacer(modifier = Modifier.height(10.dp))
            ElementOfChatsList(navController)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

