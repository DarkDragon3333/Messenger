package com.example.messenger.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.messenger.screens.componentOfScreens.ContactCard
import com.example.messenger.utilsFilies.commonModalContactList

@Composable
fun ContactsScreen() {
    LazyColumn {
        items(commonModalContactList){ contact ->
            ContactCard(contact)
        }
    }
}