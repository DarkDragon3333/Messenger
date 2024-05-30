package com.example.messenger.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.utilis.mainFieldStyle

@Composable
fun ChatScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(0.dp, 700.dp, 0.dp, 0.dp))

        mainFieldStyle(
            "Введите сообщение",
            true,
            50
        ){

        }
    }
}
