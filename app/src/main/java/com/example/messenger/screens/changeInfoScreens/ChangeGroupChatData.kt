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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messenger.screens.componentOfScreens.ContactCard
import com.example.messenger.utils.UriImage
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.mainFieldStyle
import com.example.messenger.utils.makeToast
import com.example.messenger.viewModals.GroupChatViewModal

@Composable
fun ChangeGroupChatData(groupChatViewModal: GroupChatViewModal) {
    var text = ""
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UriImage(192.dp, groupChatViewModal.photoUrl.value) { }
        val groupChatName = mainFieldStyle(
            labelText = "Измените название чата",
            enable = true,
            maxLine = 1,
            groupChatViewModal.groupChatName.value
        ) {}

        HorizontalDivider()
        ContactsList(groupChatViewModal)
    }
}

@Composable
fun ContactsList(groupChatViewModal: GroupChatViewModal) {
    LazyColumn (modifier = Modifier.fillMaxWidth()){
        items(groupChatViewModal.getContactsData(), key = { it.id }) { contact ->
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.CenterEnd,
            ) {
                ContactCard(contact)
                Row(
                    modifier = Modifier.clickable {
                        if (groupChatViewModal.getContactsData().size > 2)
                            groupChatViewModal.getContactsData().remove(contact)
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