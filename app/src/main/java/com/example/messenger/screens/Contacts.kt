package com.example.messenger.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.messenger.screens.componentOfScreens.ContactCard
import com.example.messenger.utilsFilies.contactsList
import com.example.messenger.utilsFilies.mapContacts

@Composable
fun ContactsScreen() {
    LazyColumn {
        items(mapContacts.size){contact ->
            ContactCard(mapContacts[contactsList[contact]])
        }
    }
}