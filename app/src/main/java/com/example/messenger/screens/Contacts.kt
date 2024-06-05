package com.example.messenger.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.screens.componentOfScreens.ContactCard
import com.example.messenger.utilsFilies.contactsListUSER
import com.example.messenger.utilsFilies.mapContacts

@Composable
fun ContactsScreen(navController: NavHostController) {
    LazyColumn {
        items(mapContacts.size){contact ->
            ContactCard(mapContacts[contactsListUSER[contact]], navController)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}