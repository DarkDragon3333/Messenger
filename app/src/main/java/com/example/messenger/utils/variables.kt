package com.example.messenger.utils

import android.net.Uri
import com.example.messenger.MainActivity
import com.example.messenger.modals.ContactModal

var on_settings_screen = false
var flagDropMenuButtonOnSettingsScreen = -1
var flagNavButtonOnChatsScreen = 1
var flagNavButtonOnGroupChatScreen = -1
var flagNavButtonOnChatScreen = -1
var get_out_from_auth = false
var sign_out = false
var sign_in = true



val contactsListUSER: MutableList<String> = mutableListOf()
val mapContacts: MutableMap<String, ContactModal> = mutableMapOf()

//var cacheMessages: MutableList<CommonModal> = mutableStateListOf()

lateinit var mainActivityContext: MainActivity
lateinit var defaultImageUri: Uri

