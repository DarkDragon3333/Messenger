package com.example.messenger.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.messenger.screens.SelectDataForGroupChat
import com.example.messenger.screens.navMenu.ContactsScreen
import com.example.messenger.screens.navMenu.SearchScreen
import com.example.messenger.screens.navMenu.SettingsScreen
import com.example.messenger.screens.navMenu.YourProfile
import com.example.messenger.screens.changeInfoScreens.ChangeBIO
import com.example.messenger.screens.changeInfoScreens.ChangeName
import com.example.messenger.screens.changeInfoScreens.ChangePhotoUrl
import com.example.messenger.screens.changeInfoScreens.ChangeUserName
import com.example.messenger.screens.chatScreens.ChatScreen
import com.example.messenger.screens.chatScreens.ChatsScreen
import com.example.messenger.screens.chatScreens.GroupChat
import com.example.messenger.screens.navMenu.SelectUsers

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
        { backStackEntry ->
            val fullname = backStackEntry.arguments?.getString("fullname")
            val status = backStackEntry.arguments?.getString("status")
            val photoURL = backStackEntry.arguments?.getString("photoURL")
            val id = backStackEntry.arguments?.getString("id").toString()

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
        composable(Screens.SelectUsers.route) {
            SelectUsers(navController)
        }
        composable(
            "groupChat/{groupChatName}/{photoUrlGroupChat}/{contactList}",
            arguments = listOf(
                navArgument("groupChatName") { type = NavType.StringType},
                navArgument("photoUrlGroupChat") { type = NavType.StringType},
                navArgument("contactList") { type = NavType.StringArrayType }
            )
        ) { backStackEntry ->
            val groupChatName = backStackEntry.arguments?.getString("groupChatName")
            val photoUrlGroupChat = backStackEntry.arguments?.getString("photoUrlGroupChat")
            val contactList = backStackEntry.arguments?.getString("contactList")

            GroupChat(
                navController,
                contactList,
                groupChatName.toString(),
                photoUrlGroupChat.toString()
            )
        }

        composable(
            "selectData/{contactList}"
        ) { backStackEntry ->
            val contactList = backStackEntry.arguments?.getString("contactList")

            SelectDataForGroupChat(navController, contactList.toString())
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



