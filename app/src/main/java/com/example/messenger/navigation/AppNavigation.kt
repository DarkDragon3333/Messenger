package com.example.messenger.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.messenger.screens.SettingsScreen

@Composable
fun DrawerNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.Inbox.route
    ) {
        composable(Screens.YourProfile.route){
            YourProfile()
        }
        composable(Screens.Inbox.route) {
            InboxScreen()
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
            SettingsScreen()
        }
        composable(Screens.ChangePhoneNumber.route) {
            ChangePhoneNumber()
        }
    }
}

@Composable
fun ChangePhoneNumber() {
    Text(text = "ChangePhoneNumber Screen", fontSize = 30.sp)
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
fun InboxScreen() {
    Text(text = "Inbox Screen", fontSize = 30.sp)
}

@Composable
fun YourProfile() {
    Text(text = "Your Profile Screen", fontSize = 30.sp)
}