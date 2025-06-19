package com.example.messenger.screens.componentOfScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messenger.modals.ContactModal
import com.example.messenger.utils.UriImage

@Composable
fun ContactCard(user: ContactModal?) {
    if (user != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.padding(8.dp))

            UriImage(dp = 54.dp, user.photoUrl) {}

            Spacer(modifier = Modifier.padding(4.dp))
            Column {
                Text(text = user.fullname)
                Text(text = user.phone)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp, 30.dp, 0.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(text = user.status)
            }
        }
    }
}

@Composable
fun AddContactCard(user: ContactModal?) {
    if (user != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.padding(8.dp))

            UriImage(dp = 54.dp, user.photoUrl) {}

            Spacer(modifier = Modifier.padding(4.dp))
            Column {
                Text(text = user.fullname)
                Text(text = user.phone)
            }
        }
    }
}