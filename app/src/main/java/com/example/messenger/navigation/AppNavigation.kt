package com.example.messenger.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.messenger.changeInfo.ChangeBIO
import com.example.messenger.changeInfo.ChangeName
import com.example.messenger.changeInfo.ChangePhotoUrl
import com.example.messenger.changeInfo.ChangeUserName
import com.example.messenger.screens.SettingsScreen

@Composable
fun DrawerNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.Chats.route
    ) {
        composable(Screens.YourProfile.route) {
            YourProfile()
        }
        composable(Screens.Chats.route) {
            ChatsScreen()
        }
        composable(Screens.Sent.route) {
            SentScreen()
        }
        composable(Screens.Starred.route) {
            StarredScreen()
        }
        composable(Screens.Spam.route) {
            SpamScreen()
        }
        composable(Screens.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screens.ChangeName.route) {
            ChangeName(navController)
        }
        composable(Screens.ChangeBIO.route) {
            ChangeBIO(navController)
        }
        composable(Screens.ChangeUserName.route) {
            ChangeUserName(navController)
        }
        composable(Screens.ChangePhotoUrl.route) {
            ChangePhotoUrl(navController)
        }
    }
}

@Composable
fun SpamScreen() {
    Text(text = "Sent Screen", fontSize = 30.sp)
}

@Composable
fun StarredScreen() {
    Text(text = "Starred Screen", fontSize = 30.sp)
}

@Composable
fun SentScreen() {
    Text(text = "Spam Screen", fontSize = 30.sp)
}

@Composable
fun ChatsScreen() {
    Text(text = "Chats Screen", fontSize = 30.sp)
}

@Composable
fun YourProfile() {
    Text(text = "Your Profile Screen", fontSize = 30.sp)
}