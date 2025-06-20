package com.example.messenger.screens.changeInfoScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.dataBase.firebaseFuns.choseChangeInformation
import com.example.messenger.utils.Constants.CHILD_CHAT_NAME
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.MainFieldStyle
import com.example.messenger.utils.makeToast

@Composable
fun ChangeName(navController: NavHostController) {
    val fullname = remember { USER.fullname.split(" ") }
    var name by rememberSaveable { mutableStateOf(fullname[0]) }
    var surname by rememberSaveable { mutableStateOf(if (fullname.size == 2) fullname[1] else "") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "Введите новое имя и фамилию:")

        Spacer(modifier = Modifier.height(10.dp))
        MainFieldStyle(
            labelText = "Введите имя",
            enable = true,
            maxLine = 1,
            text = name,
        ) { newName ->
            name = newName
        }

        Spacer(modifier = Modifier.height(10.dp))
        MainFieldStyle(
            labelText = "Введите фамилию",
            enable = true,
            maxLine = 1,
            text = surname,
        ) { newSurname ->
            surname = newSurname
        }

        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {
                if (name.isBlank()) {
                    makeToast("Введите имя", mainActivityContext)
                } else {
                    choseChangeInformation(
                        "$name $surname".trim(),
                        CHILD_CHAT_NAME,
                        mainActivityContext,
                        navController
                    )
                }
            }
        ) {
            Icon(Icons.Default.Check, contentDescription = "")
            Text("Подтвердить")
        }
    }
}




