package com.example.messenger.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.messenger.navigation.DrawerNavigation
import com.example.messenger.navigation.Screens
import com.example.messenger.user_sing_in_and_up.LoginActivity
import com.example.messenger.utilis.AUTH
import com.example.messenger.utilis.USER
import com.example.messenger.utilis.goTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavDrawer() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Screens.Chats
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var flagYouInSettings by remember { mutableIntStateOf(-1) }
    var flagYouInChats by remember { mutableIntStateOf(1) }

    val title = ""

    val screens = listOf( //Созданные экраны в виде объектов
        Screens.YourProfile,
        Screens.Chats,
        Screens.Sent,
        Screens.Starred,
        Screens.Spam,
        Screens.Settings,
        Screens.ChangeName
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {//Элемент выдвигающегося меню
                HorizontalDivider(modifier = Modifier.padding(bottom = 20.dp), thickness = 1.dp)

                Column(modifier = Modifier.padding(15.dp, 0.dp)) {
                    Spacer(modifier = Modifier.padding(10.dp))
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            navController.navigate(Screens.YourProfile.route) {//Используем navController для перемещения по экранам
                                launchSingleTop = true
                            }
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        })
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(text = USER.fullname)
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(text = USER.phone)
                }
                Spacer(modifier = Modifier.padding(10.dp)) //Отступ
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) //Линия

                screens.forEach { screen ->//Циклом генерируем элементы выдвигающегося меню
                    if (screen != Screens.Chats) {
                        if (screen != Screens.ChangeName) {
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
                                onClick = {
                                    navController.navigate(screen.route) {//Используем navController для перемещения по экранам
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
                    actions = {//Элементы в конце TopAppBar
                        navController.addOnDestinationChangedListener { _, destination, _ -> //Хочу, чтобы выпадающий список появлялся только на странице настроек
                            flagYouInSettings =
                                if ((destination.route == Screens.Settings.route)) {
                                    1
                                } else {
                                    -1
                                }
                        }
                        if (flagYouInSettings == 1) {
                            DropdownMenuItems(drawerState, coroutineScope, navController)
                        }

                    },

                    navigationIcon = {
                        navController.addOnDestinationChangedListener { _, destination, _ ->
                            flagYouInChats =
                                if (destination.route == Screens.Chats.route) {
                                    1

                                } else {
                                    -1
                                }
                        }
                        if (flagYouInChats == 1) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch { drawerState.open() }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu icon"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        navController.navigate(Screens.Settings.route)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back icon"
                                )
                            }
                        }

                    },
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

@Composable
fun DropdownMenuItems(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    navController: NavController,
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box {
        Row {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Показать меню")
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    navController.navigate(Screens.ChangeName.route) {//Используем navController для перемещения по экранам
                        launchSingleTop = true
                    }
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                text = { Text("Изменить ФИО") }
            )
            DropdownMenuItem(
                onClick = {  },
                text = { Text("Изменить фото") }
            )
            HorizontalDivider()
            DropdownMenuItem(
                onClick = {
                    AUTH.signOut()
                    goTo(LoginActivity::class.java, context)
                },
                text = { Text("Выйти из аккаунта") }
            )
        }
    }
}




