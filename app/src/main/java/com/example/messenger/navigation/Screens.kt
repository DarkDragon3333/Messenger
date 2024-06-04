package com.example.messenger.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
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
        title = "Чаты",
        route = "чаты",
        icon = Icons.Default.Email
    )
    data object Chat: Screens(
        title = "Чат",
        route = "чат",
        icon = Icons.Default.Email
    )
    data object Contacts: Screens(
        title = "Контакты",
        route = "контакты",
        icon = Icons.AutoMirrored.Filled.Send
    )
    data object Starred: Screens(
        title = "Starred",
        route = "starred",
        icon = Icons.Default.Star
    )
    data object Spam: Screens(
        title = "Спам",
        route = "спам",
        icon = Icons.Default.Warning
    )
    data object Settings: Screens(
        title = "Настройки",
        route = "настройки",
        icon = Icons.Default.Settings
    )
    data object YourProfile: Screens(
        title = "Ваш профиль",
        route = "ваш профиль",
        icon = Icons.Default.AccountCircle
    )
    data object ChangeName: Screens(
        title = "Смена имени",
        route = "смена имени",
        icon = Icons.Default.Edit
    )
    data object ChangeUserName: Screens(
        title = "Смена никнейма",
        route = "смена никнейма",
        icon = Icons.Default.Edit
    )
    data object ChangeBIO: Screens (
        title = "Смена описания",
        route = "смена описания",
        icon = Icons.Default.Edit
    )
    data object ChangePhotoUrl: Screens (
        title = "Смена фото профиля",
        route = "смена фото профиля",
        icon = Icons.Default.Edit
    )
    data object Search: Screens(
        title = "Поиск",
        route = "поиск",
        icon = Icons.AutoMirrored.Filled.Send
    )
}
