package com.example.messenger.utilsFilies

import com.example.messenger.MainActivity
import com.example.messenger.modals.CommonModal

var on_settings_screen = false
var flagDropMenuButtonOnSettingsScreen = -1
var flagNavButtonOnChatsScreen = 1
var get_out_from_auth = false
var sign_out = false
var sign_in = true

var sizeContactsList = 0
val contactsList: MutableList<CommonModal> = mutableListOf();

val commonModalContactList: MutableList<CommonModal> = mutableListOf();

lateinit var mainActivityContext: MainActivity


