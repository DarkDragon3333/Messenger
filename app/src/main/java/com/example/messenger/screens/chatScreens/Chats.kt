package com.example.messenger.screens.chatScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.messenger.screens.componentOfScreens.ElementOfChatsList

@Composable
fun ChatsScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        ElementOfChatsList(navController)
    }
}

