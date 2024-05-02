package com.example.messenger.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import com.example.messenger.R

data class NavItems(
//    var label: String,
    val selectedIcon: Int,
    val route: String
)

 val listOfNavItems: List<NavItems> = listOf(
    NavItems(
//        label = "AlarmScreen",
        selectedIcon = R.drawable.ic_launcher_background,
        route = Screens.AlarmScreen.name
    ), NavItems(
//        label = "MusicScreen",
        selectedIcon = R.drawable.ic_launcher_background,
        route = Screens.MusicScreen.name
    ), NavItems(
//        label = "ArticlesScreen",
        selectedIcon = R.drawable.ic_launcher_background,
        route = Screens.ArticlesScreen.name
    )
)