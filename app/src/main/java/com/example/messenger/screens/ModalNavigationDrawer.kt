package com.example.messenger.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.messenger.navigation.DrawerNavigation
import com.example.messenger.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavDrawer() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Screens.Inbox
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val screens = listOf(
        Screens.YourProfile,
        Screens.Inbox,
        Screens.Sent,
        Screens.Starred,
        Screens.Spam,
        Screens.Settings
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Divider(thickness = 1.dp, modifier = Modifier.padding(bottom = 20.dp))

                Column(modifier = Modifier.padding(15.dp, 0.dp)) {
                    Spacer(modifier = Modifier.padding(10.dp))
                    Icon(Icons.Default.Person, contentDescription = "", modifier = Modifier.clickable {
                        navController.navigate(Screens.YourProfile.route) {
                            launchSingleTop = true
                        }
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    })
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(text = "Максим")
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(text = "+7 918 898 98-98")
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Divider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp))
                screens.forEach { screen ->
                    NavigationDrawerItem(
                        label = { Text(text = screen.title) },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = "${screen.title} icon"
                            )
                        },
                        selected = currentRoute == screen.route,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        badge = {
                            screen.badgeCount?.let {
                                Badge(
                                    modifier = Modifier.size(30.dp),
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(text = screen.badgeCount.toString())
                                }
                            }
                        },
                        onClick = {
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                            }
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }

            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentRoute.toString().replaceFirstChar { it.uppercase() })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu icon")
                        }
                    }
                )
            }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ) {
                DrawerNavigation(navController)
            }
        }
    }
}




