package com.example.messenger.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.messenger.screens.ContactsScreen
import com.example.messenger.screens.SearchScreen
import com.example.messenger.screens.SettingsScreen
import com.example.messenger.screens.changeInfoScreens.ChangeBIO
import com.example.messenger.screens.changeInfoScreens.ChangeName
import com.example.messenger.screens.changeInfoScreens.ChangePhotoUrl
import com.example.messenger.screens.changeInfoScreens.ChangeUserName
import com.example.messenger.screens.chatScreens.ChatScreen
import com.example.messenger.screens.chatScreens.ChatsScreen

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
            ChatsScreen(navController)
        }
        composable(
            "chatScreen/{fullname}/{status}/{photoURL}/{id}",
            arguments = listOf(
                navArgument("fullname") { type = NavType.StringType},
                navArgument("status") { type = NavType.StringType},
                navArgument("photoURL") { type = NavType.StringType},
                navArgument("id") { type = NavType.StringType}
            )
        )
        {backStackEntry ->
            val fullname = backStackEntry.arguments?.getString("fullname")
            val status = backStackEntry.arguments?.getString("status")
            val photoURL = backStackEntry.arguments?.getString("photoURL")
            val id = backStackEntry.arguments?.getString("id")

            ChatScreen(fullname, status, photoURL, id, navController)
        }
        composable(Screens.Contacts.route) {
            ContactsScreen(navController)
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
        composable(Screens.Search.route) {
            SearchScreen(navController)
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
fun YourProfile() {
    Text(text = "Your Profile Screen", fontSize = 30.sp)
}