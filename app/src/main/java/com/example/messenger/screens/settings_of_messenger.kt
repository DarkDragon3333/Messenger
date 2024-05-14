package com.example.messenger.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {
    Column {
        HeaderOfSettings()

        BodyOfSettings()

        FooterOfSettings()
    }


}

@Composable
fun HeaderOfSettings() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.15f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.padding(4.dp))
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = "AccountCircle",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Column {
            Text(text = "Имя", color = Color.Black)
            Text(text = "Статус", color = Color.Black)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 30.dp, 0.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Column {
                FloatingActionButton(onClick = { /*TODO*/ }, ) {
                    Icon(Icons.Default.Add, contentDescription = "")
                }
                Spacer(modifier = Modifier.padding(0.dp, 2.dp, 0.dp, 0.dp))
                Text(text = "Новое фото", fontSize = 10.sp)
            }
            
        }

    }
    Spacer(modifier = Modifier.padding(4.dp))
    Divider(thickness = 5.dp, modifier = Modifier.padding(bottom = 10.dp))
}

@Composable
fun BodyOfSettings() {
    var account by remember { mutableStateOf("") }
    var idAccount by remember { mutableStateOf("") }
    var aboutYou by remember { mutableStateOf("") }
    Column {
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = "Аккаунт", modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp))
        Spacer(modifier = Modifier.padding(16.dp))

        TextField(
            value = account,
            onValueChange = { account = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("") },
            label = { Text(text = "Нажмите, чтобы изменить номер телефона", fontSize = 12.sp) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                unfocusedTextColor = Color(0xff888888),
                focusedContainerColor = Color.White,
                focusedTextColor = Color(0xff222222),
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = idAccount,
            onValueChange = { idAccount = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("") },
            label = { Text(text = "Имя пользователя", fontSize = 12.sp) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                unfocusedTextColor = Color(0xff888888),
                focusedContainerColor = Color.White,
                focusedTextColor = Color(0xff222222),
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = aboutYou,
            onValueChange = { aboutYou = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("") },
            label = { Text(text = "Напишите немного о себе", fontSize = 12.sp) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                unfocusedTextColor = Color(0xff888888),
                focusedContainerColor = Color.White,
                focusedTextColor = Color(0xff222222),
            )
        )
    }
    Spacer(modifier = Modifier.padding(16.dp))
    Divider(thickness = 5.dp, modifier = Modifier.padding(bottom = 10.dp))
}

@Composable
fun FooterOfSettings() {
    Spacer(modifier = Modifier.padding(8.dp))
    Column {
        Text(text = "Настройки", modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp))
        Spacer(modifier = Modifier.padding(16.dp))

        val arrayOfIcons = listOf(Icons.Default.Notifications, Icons.Default.Lock)
        val arrayOfName = listOf("Уведомление и звук", "Конфиденциальность")
        var index = 0

        while (index < arrayOfIcons.size) {
            ElementOfFooter(arrayOfIcons[index], arrayOfName[index], index)
            index++
        }

    }
}

@Composable
fun ElementOfFooter(lock: ImageVector, s: String, index: Int) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(10.dp, 0.dp, 0.dp, 0.dp)
            .clickable {
                Toast
                    .makeText(context, s, Toast.LENGTH_SHORT)
                    .show()
            }
    ) {
        Icon(lock, contentDescription = "")
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(text = s)
            Spacer(modifier = Modifier.padding(8.dp))
            Divider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp))
        }

    }
}

@Composable
fun ListenerOfElement(index: Int){
    if (index == 0){
        MakeToast(msg = "Конфиденциальность")
    } else if (index == 1){
        MakeToast(msg = "Уведомление и звук")
    }
}

@Composable
fun MakeToast(msg: String){
    val context = LocalContext.current
    Toast
        .makeText(context, msg, Toast.LENGTH_SHORT)
        .show()
}