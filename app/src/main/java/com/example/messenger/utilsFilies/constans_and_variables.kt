package com.example.messenger.utilsFilies

import com.example.messenger.MainActivity
import com.example.messenger.modals.CommonModal

var on_settings_screen = false
var flagDropMenuButtonOnSettingsScreen = -1
var flagNavButtonOnChatsScreen = 1
var flagNavButtonOnChatScreen = -1
var get_out_from_auth = false
var sign_out = false
var sign_in = true


val contactsListUSER: MutableList<String> = mutableListOf()
val mapContacts: MutableMap<String, CommonModal> = mutableMapOf()

lateinit var mainActivityContext: MainActivity


