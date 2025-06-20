package com.example.messenger.screens.changeInfoScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messenger.modals.ContactModal
import com.example.messenger.screens.componentOfScreens.ContactCard
import com.example.messenger.utils.UriImage
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.MainFieldStyle
import com.example.messenger.utils.makeToast
import com.example.messenger.viewModals.GroupChatViewModal

@Composable
fun ChangeGroupChatData(groupChatViewModal: GroupChatViewModal) {
    var groupChatName by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(20.dp))
        UriImage(192.dp, groupChatViewModal.photoUrl.value) { }
        Spacer(modifier = Modifier.padding(20.dp))

        MainFieldStyle(
            labelText = "Измените название чата",
            enable = true,
            maxLine = 1,
            groupChatViewModal.groupChatName.value
        ) { name ->
            groupChatName = name
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.padding(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Spacer(modifier = Modifier.padding(8.dp))
            Text("Участники группы: ")
        }
        Spacer(modifier = Modifier.padding(10.dp))
        ContactsList(groupChatViewModal)
    }
}

@Composable
fun ContactsList(groupChatViewModal: GroupChatViewModal) {
    val usersList =
        remember { mutableStateListOf<ContactModal>().apply { addAll(groupChatViewModal.getContactsData()) } }
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(usersList, key = { it.id }) { contact ->
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.CenterEnd,
            ) {
                ContactCard(contact)
                Row(
                    modifier = Modifier.clickable {
                        if (usersList.size > 2)
                            usersList.remove(contact)
                        else
                            makeToast("Слишком мало учатников", mainActivityContext)
                    }
                ) {
                    Icon(
                        Icons.Filled.Clear,
                        ""
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}