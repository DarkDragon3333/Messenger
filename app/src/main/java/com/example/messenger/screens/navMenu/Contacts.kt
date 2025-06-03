package com.example.messenger.screens.navMenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.screens.componentOfScreens.ContactCard
import com.example.messenger.utils.contactsListUSER
import com.example.messenger.utils.goTo
import com.example.messenger.utils.mapContacts

@Composable
fun ContactsScreen(navController: NavHostController) {
    LazyColumn{
        items(mapContacts.size) { contact ->
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        goTo(navController, mapContacts[contactsListUSER[contact]]!!)
                    }
            ) {
                ContactCard(mapContacts[contactsListUSER[contact]])
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}