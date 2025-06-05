package com.example.messenger.screens.navMenu.createNewGroupChat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.modals.ContactModal
import com.example.messenger.navigation.Screens
import com.example.messenger.screens.componentOfScreens.ContactCard
import com.example.messenger.utils.contactsListUSER
import com.example.messenger.utils.goTo
import com.example.messenger.utils.mapContacts

@Composable
fun SelectUsers(navController: NavHostController) {
    val contactsList = remember { mutableListOf<ContactModal>() }

    Box(contentAlignment = Alignment.BottomEnd) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(mapContacts.size) { contact ->
                val check = remember { mutableStateOf(false) }

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                            }
                    ) {
                        Checkbox(
                            modifier = Modifier,
                            checked = check.value,
                            onCheckedChange = {
                                check.value = it

                                when (check.value) {
                                    true -> {
                                        if (!contactsList.contains(mapContacts[contactsListUSER[contact]])) {
                                            contactsList.add(mapContacts[contactsListUSER[contact]]!!)
                                        }
                                    }

                                    false -> {
                                        if (contactsList.contains(mapContacts[contactsListUSER[contact]])) {
                                            contactsList.remove(mapContacts[contactsListUSER[contact]]!!)
                                        }
                                    }
                                }
                            }
                        )
                        ContactCard(mapContacts[contactsListUSER[contact]])
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape),
            onClick = {
                goTo(
                    navController,
                    Screens.SelectDataForGroupChat,
                    contactsList
                )
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = ""
            )
        }
    }
}
