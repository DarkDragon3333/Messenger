package com.example.messenger.screens.componentOfScreens

import androidx.compose.foundation.clickable
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
import androidx.navigation.NavHostController
import com.example.messenger.modals.ContactModal
import com.example.messenger.utilsFilies.UriImage
import com.example.messenger.utilsFilies.goTo

@Composable
fun ContactCard(user: ContactModal?, navController: NavHostController) {
    if (user != null) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable {
                goTo(navController, user)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.padding(8.dp))

            UriImage(dp = 64.dp, user.photoUrl) {}

            Spacer(modifier = Modifier.padding(4.dp))
            Column {
                Text(text = user.fullname)
                Text(text = user.phone)
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Text(text = user.status)
            }
        }
    }
}
