package com.example.messenger.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.messenger.modals.ContactModal
import com.example.messenger.screens.navMenu.createNewGroupChat.SelectDataForGroupChat
import com.example.messenger.screens.navMenu.ContactsScreen
import com.example.messenger.screens.navMenu.SearchScreen
import com.example.messenger.screens.navMenu.SettingsScreen
import com.example.messenger.screens.navMenu.YourProfile
import com.example.messenger.screens.changeInfoScreens.ChangeBIO
import com.example.messenger.screens.changeInfoScreens.ChangeGroupChatData
import com.example.messenger.screens.changeInfoScreens.ChangeName
import com.example.messenger.screens.changeInfoScreens.ChangePhotoUrl
import com.example.messenger.screens.changeInfoScreens.ChangeUserName
import com.example.messenger.screens.chatScreens.ChatScreen
import com.example.messenger.screens.chatScreens.ChatsScreen
import com.example.messenger.screens.chatScreens.GroupChat
import com.example.messenger.screens.navMenu.createNewGroupChat.SelectUsers
import com.example.messenger.viewModals.ChatsViewModal
import com.example.messenger.viewModals.ContactsViewModal
import com.example.messenger.viewModals.CurrentChatHolderViewModal
import com.example.messenger.viewModals.GroupChatViewModal


@Composable
fun DrawerNavigation(
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal,
    contactsViewModal: ContactsViewModal,
    chatsViewModal: ChatsViewModal,
    groupChatViewModal: GroupChatViewModal
) {

    NavHost(
        navController = navController,
        startDestination = Screens.Chats.route
    ) {
        composable(Screens.YourProfile.route) {
            YourProfile()
        }
        composable(Screens.Chats.route) {
            ChatsScreen(navController, currentChatViewModel, chatsViewModal)
        }
        composable(Screens.Chat.route) {
            ChatScreen(navController, currentChatViewModel)
        }
        composable(Screens.GroupChat.route) {
            GroupChat(navController, currentChatViewModel)
        }
        composable(Screens.Contacts.route) {
            ContactsScreen(navController, currentChatViewModel, contactsViewModal)
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
        composable(
            route = "${Screens.ChangeName.route}?t={t}",
            arguments = listOf(navArgument("t") {
                defaultValue = ""
            })
        ) {
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
            SelectUsers(navController, currentChatViewModel, contactsViewModal)
        }

        composable(Screens.SelectDataForGroupChat.route) {
            val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
            val contactsList = savedStateHandle?.get<MutableList<ContactModal>>("contactList")

            if (contactsList != null) {
                SelectDataForGroupChat(navController, contactsList, currentChatViewModel)
            }
        }
        composable(Screens.ChangeGroupChatData.route){
            ChangeGroupChatData(groupChatViewModal)
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



