package com.example.messenger.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screens1 {
    AlarmScreen,
    MusicScreen,
    ArticlesScreen
}

sealed class Screens(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val badgeCount: Int? = null
) {
    data object Inbox: Screens(
        title = "Inbox",
        route = "inbox",
        icon = Icons.Default.Email,
        badgeCount = 31
    )
    data object Sent: Screens(
        title = "Sent",
        route = "sent",
        icon = Icons.Default.Send
    )
    data object Starred: Screens(
        title = "Starred",
        route = "starred",
        icon = Icons.Default.Star,
        badgeCount = 15
    )
    data object Spam: Screens(
        title = "Spam",
        route = "spam",
        icon = Icons.Default.Warning
    )
    data object Bin: Screens(
        title = "Bin",
        route = "bin",
        icon = Icons.Default.Delete
    )
    data object YourProfile: Screens(
        title = "YourProfile",
        route = "yourProfile",
        icon = Icons.Default.AccountCircle
    )
}
