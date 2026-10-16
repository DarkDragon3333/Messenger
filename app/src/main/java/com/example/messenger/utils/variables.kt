package com.example.messenger.utils

import android.net.Uri
import com.example.messenger.MainActivity
import com.example.messenger.modals.ContactModal
import com.google.firebase.storage.StorageReference

lateinit var mainActivityContext: MainActivity
lateinit var defaultImageUri: Uri
lateinit var pathToPhoto: StorageReference
lateinit var pathToSelectPhoto: StorageReference

var get_out_from_auth = false
var sign_out = false
var sign_in = true

val contactsListUSER: MutableList<String> = mutableListOf()
val mapContacts: MutableMap<String, ContactModal> = mutableMapOf()
