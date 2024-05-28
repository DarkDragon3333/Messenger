package com.example.messenger.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.changeInfo.ChangeNumber
import com.example.messenger.navigation.Screens
import com.example.messenger.utilis.MainImage
import com.example.messenger.utilis.USER
import com.example.messenger.utilis.goTo
import com.example.messenger.utilis.mainFieldStyle
import com.example.messenger.utilis.makeToast

@Composable
fun SettingsScreen(navController: NavHostController) {
    Column {
        HeaderOfSettings()
        BodyOfSettings(navController)
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

        MainImage(64.dp){}

        Spacer(modifier = Modifier.padding(4.dp))
        Column {
            Text(text = USER.fullname, color = Color.Black)
            Text(text = USER.phone, color = Color.Black)
        }

    }
    Spacer(modifier = Modifier.padding(4.dp))
    HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp), thickness = 5.dp)
}

@Composable
fun BodyOfSettings(navController: NavHostController) {
    val context = LocalContext.current

    Column {
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = "Аккаунт", modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp))

        Spacer(modifier = Modifier.padding(8.dp))
        mainFieldStyle(
            labelText = "Нажмите, чтобы изменить номер",
            enable = false,
            3,
            USER.phone
        ) {
            goTo(ChangeNumber::class.java, context)
        }

        Spacer(modifier = Modifier.padding(8.dp))
        mainFieldStyle(
            labelText = "Нажмите, чтобы изменить ваш ник",
            enable = false,
            1,
            USER.username,
        ) {
            goTo(navController, Screens.ChangeUserName)
        }

        Spacer(modifier = Modifier.padding(8.dp))
        mainFieldStyle(
            labelText = "Напишите немного о себе",
            enable = false,
            1,
            USER.bio,
        ) {
            goTo(navController, Screens.ChangeBIO)
        }
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
                makeToast(s, context)
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