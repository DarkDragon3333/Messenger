package com.example.messenger.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun DrawerNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.Inbox.route
    ) {
        composable(Screens.YourProfile.route){
            YourProfile()
        }
        composable(Screens.Inbox.route) {
            InboxScreen()
        }
        composable(Screens.Sent.route) {
            SentScreen()
        }
        composable(Screens.Starred.route) {
            StarredScreen()
        }
        composable(Screens.Spam.route) {
            SpamScreen()
        }
        composable(Screens.Bin.route) {
            BinScreen()
        }
    }
}

@Composable
fun BinScreen() {
    Text(text = "BinScreen", fontSize = 30.sp)
}

@Composable
fun SpamScreen() {
    Text(text = "Sent Screen", fontSize = 30.sp)
}

@Composable
fun StarredScreen() {
    Text(text = "Starred Screen", fontSize = 30.sp)
}

@Composable
fun SentScreen() {
    Text(text = "Spam Screen", fontSize = 30.sp)
}

@Composable
fun InboxScreen() {
    Text(text = "Inbox Screen", fontSize = 30.sp)
}

@Composable
fun YourProfile() {
    Text(text = "Your Profile Screen", fontSize = 30.sp)
}