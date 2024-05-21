package com.example.messenger.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    data object Chats: Screens(
        title = "Chats",
        route = "chats",
        icon = Icons.Default.Email
    )
    data object Sent: Screens(
        title = "Sent",
        route = "sent",
        icon = Icons.Default.Send
    )
    data object Starred: Screens(
        title = "Starred",
        route = "starred",
        icon = Icons.Default.Star
    )
    data object Spam: Screens(
        title = "Spam",
        route = "spam",
        icon = Icons.Default.Warning
    )
    data object Settings: Screens(
        title = "Settings",
        route = "Settings",
        icon = Icons.Default.Settings
    )
    data object YourProfile: Screens(
        title = "YourProfile",
        route = "yourProfile",
        icon = Icons.Default.AccountCircle
    )
    data object ChangeName: Screens(
        title = "ChangeName",
        route = "changeName",
        icon = Icons.Default.Edit
    )
    data object ChangeUserName: Screens(
        title = "ChangeUserName",
        route = "changeUserName",
        icon = Icons.Default.Edit
    )
}
