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
import com.example.messenger.modals.ChatModal
import com.example.messenger.modals.ContactModal
import com.example.messenger.navigation.Screens
import com.example.messenger.screens.componentOfScreens.ContactCard
import com.example.messenger.utils.contactsListUSER
import com.example.messenger.utils.goTo
import com.example.messenger.utils.mapContacts
import com.example.messenger.viewModals.CurrentChatHolderViewModal

@Composable
fun ContactsScreen(
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal
) {
    LazyColumn {
        items(mapContacts.size) { contact ->
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val chatModal = ChatModal(
                            mapContacts[contactsListUSER[contact]]?.fullname ?: "",
                            mapContacts[contactsListUSER[contact]]?.photoUrl ?: "",
                            mapContacts[contactsListUSER[contact]]?.id ?: "",
                            mapContacts[contactsListUSER[contact]]?.status ?: "",
                            "",
                            "",
                        )
                        currentChatViewModel.setChat(chatModal)
                        goTo(navController, Screens.Chat)

                    }
            ) {
                ContactCard(mapContacts[contactsListUSER[contact]])
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
