package com.example.messenger.viewModals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.messenger.navigation.Screens

class NavDrawerViewModal : ViewModel() {


    private val _screens = mutableStateListOf<Screens>(//Созданные экраны в виде объектов
        Screens.YourProfile,
        Screens.Chats,
        Screens.SelectUsers,
        Screens.Contacts,
        Screens.Settings,
        Screens.ChangeName
    )
    val screens: SnapshotStateList<Screens> get() = _screens

    var isSettings by mutableStateOf(false)
        private set
    var isChats by mutableStateOf(false)
        private set
    var isChat by mutableStateOf(false)
        private set
    var isGroupChat by mutableStateOf(false)
        private set

    fun getScreensList(): SnapshotStateList<Screens> {
        return _screens
    }

    fun startListeningChangeDestination(route: String?){
        isSettings = route == Screens.Settings.route
        isChats = route == Screens.Chats.route
        isChat = route == Screens.Chat.route
        isGroupChat = route == Screens.GroupChat.route
    }

    fun getVisibleScreens(): List<Screens> {
        return _screens.filter { it != Screens.Chats && it != Screens.ChangeName }
    }
}