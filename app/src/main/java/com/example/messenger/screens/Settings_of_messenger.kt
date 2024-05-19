package com.example.messenger.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.navigation.compose.rememberNavController
import com.example.messenger.changeNumberPhone.ChangeNumber

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
    Spacer(modifier = Modifier.padding(4.dp))
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

    }
    Spacer(modifier = Modifier.padding(4.dp))
    HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp), thickness = 5.dp)
}

@Composable
fun BodyOfSettings() {
    var phone by remember { mutableStateOf("") }
    var idAccount by remember { mutableStateOf("") }
    var aboutYou by remember { mutableStateOf("") }
    val navController = rememberNavController()
    val context = LocalContext.current

    Column {
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = "Аккаунт", modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp))
        Spacer(modifier = Modifier.padding(16.dp))

        TextField(
            value = phone,
            onValueChange = { phone = it },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    context.startActivity(Intent(context, ChangeNumber::class.java))
                },
            placeholder = { Text("+7 916 987 31-31") },
            label = { Text(text = "Нажмите, чтобы изменить номер", fontSize = 12.sp) },

            enabled = false,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFDFAFE),
                unfocusedTextColor = Color(0xff888888),

                focusedContainerColor = Color(0xFFFDFAFE),
                focusedTextColor = Color(0xff222222),

                disabledContainerColor = Color(0xFFFDFAFE),
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color(0xff222222)

            )
        )
        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = idAccount,
            onValueChange = { idAccount = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("") },
            maxLines = 1,
            label = { Text(text = "Имя пользователя", fontSize = 12.sp) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFDFAFE),
                unfocusedTextColor = Color(0xff888888),
                focusedContainerColor = Color(0xFFFDFAFE),
                focusedTextColor = Color(0xff222222),
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = aboutYou,
            onValueChange = { aboutYou = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("") },
            maxLines = 3,
            label = { Text(text = "Напишите немного о себе", fontSize = 12.sp) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFDFAFE),
                unfocusedTextColor = Color(0xff888888),
                focusedContainerColor = Color(0xFFFDFAFE),
                focusedTextColor = Color(0xff222222),
            )
        )
    }
    Spacer(modifier = Modifier.padding(16.dp))
    HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp), thickness = 5.dp)
}

@Composable
fun FooterOfSettings() {
    Spacer(modifier = Modifier.padding(8.dp))
    Column {
        Text(text = "Настройки", modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp))
        Spacer(modifier = Modifier.padding(16.dp))

        val arrayOfIcons = listOf(
            Icons.Default.Notifications,
            Icons.Default.Lock,
            Icons.Default.Settings
        )
        val arrayOfName = listOf(
            "Уведомление и звук",
            "Конфиденциальность",
            "Тема"
        )
        var index = 0

        while (index < arrayOfIcons.size) {
            ElementOfFooter(arrayOfIcons[index], arrayOfName[index])
            index++
        }

    }
}

@Composable
fun ElementOfFooter(lock: ImageVector, s: String) {
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
            HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp), thickness = 1.dp)
        }

    }
}