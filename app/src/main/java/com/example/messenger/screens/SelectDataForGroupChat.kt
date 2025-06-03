package com.example.messenger.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.example.messenger.modals.ContactModal
import com.example.messenger.navigation.Screens
import com.example.messenger.utils.MainFieldStyle
import com.example.messenger.utils.goTo
import java.util.ArrayList

@Composable
fun SelectDataForGroupChat(navController: NavHostController, contactList: String?) {
    Column {
        var groupChatName = remember { mutableStateOf("") }

        Text("Название группового чата")
        MainFieldStyle(
            "Напишите имя",
            true,
            1,
            KeyboardOptions(keyboardType = KeyboardType.Text),
            groupChatName.value
        ) {}

        Button(
            onClick = {
                goTo(
                    navController,
                    contactList,
                    groupChatName.value.toString(),
                    ""
                )
            }
        ) {
            Text("Создать групповой чат")
        }
    }
}