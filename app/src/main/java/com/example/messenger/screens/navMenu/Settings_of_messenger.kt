package com.example.messenger.screens.navMenu

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.navigation.Screens
import com.example.messenger.screens.changeInfoScreens.ChangeNumber
import com.example.messenger.utils.UriImage
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.utils.goTo
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.MainFieldStyle
import com.example.messenger.utils.makeToast

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

        UriImage(64.dp, USER.photoUrl) {}

        Spacer(modifier = Modifier.padding(4.dp))
        Column {
            Text(text = USER.fullname)
            Text(text = USER.status)
        }

    }
    Spacer(modifier = Modifier.padding(4.dp))
    HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp), thickness = 5.dp)
}

@Composable
fun BodyOfSettings(navController: NavHostController) {
    Column {
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = "Аккаунт", modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp))

        Spacer(modifier = Modifier.padding(8.dp))
        MainFieldStyle(
            labelText = "Нажмите, чтобы изменить номер",
            enable = false,
            3,
            USER.phone
        ) { _ ->
            goTo(ChangeNumber::class.java, mainActivityContext)
        }

        Spacer(modifier = Modifier.padding(8.dp))
        MainFieldStyle(
            labelText = "Нажмите, чтобы изменить ваш ник",
            enable = false,
            1,
            USER.username,
        ) { _ ->
            goTo(navController, Screens.ChangeUserName)
        }

        Spacer(modifier = Modifier.padding(8.dp))
        MainFieldStyle(
            labelText = "Напишите немного о себе",
            enable = false,
            1,
            USER.bio,
        ) { _ ->
            goTo(navController, Screens.ChangeBIO)
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))
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
    Row(
        modifier = Modifier
            .padding(10.dp, 0.dp, 0.dp, 0.dp)
            .clickable {
                makeToast(s, mainActivityContext)
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