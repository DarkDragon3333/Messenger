package com.example.messenger.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.messenger.navigation.DrawerNavigation
import com.example.messenger.navigation.Screens
import com.example.messenger.user_sing_in_and_up_activities.LoginActivity
import com.example.messenger.utilsFilies.AUTH
import com.example.messenger.utilsFilies.AppStatus
import com.example.messenger.utilsFilies.MainImage
import com.example.messenger.utilsFilies.NavIconButton
import com.example.messenger.utilsFilies.USER
import com.example.messenger.utilsFilies.flagDropMenuButtonOnSettingsScreen
import com.example.messenger.utilsFilies.flagNavButtonOnChatScreen
import com.example.messenger.utilsFilies.flagNavButtonOnChatsScreen
import com.example.messenger.utilsFilies.get_out_from_auth
import com.example.messenger.utilsFilies.goTo
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.on_settings_screen
import com.example.messenger.utilsFilies.sign_in
import kotlinx.coroutines.CoroutineScope
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavDrawer() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Screens.Chats
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val screens = listOf( //Созданные экраны в виде объектов
        Screens.YourProfile,
        Screens.Chats,
        Screens.Contacts,
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
                    MainImage(64.dp, USER.photoUrl) {
                        goTo(
                            navController,
                            Screens.YourProfile,
                            coroutineScope,
                            drawerState
                        )
                    }

                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(text = USER.fullname)
                    Spacer(modifier = Modifier.padding(5.dp))
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
                                    goTo(navController, screen, coroutineScope, drawerState)
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
                        if (flagNavButtonOnChatScreen == 1) {
                            var fullname = " "
                            var statusUSER = " "
                            var photoURL = " "
                            //var id = " "
                            navController.addOnDestinationChangedListener{ _, destination, bundle ->
                                if ((bundle != null) && (destination.route == "chatScreen/{fullname}/{status}/{photoURL}/{id}")) {
                                    fullname = URLDecoder.decode(bundle.getString("fullname").toString(), "UTF-8")
                                    statusUSER = URLDecoder.decode(bundle.getString("status").toString(), "UTF-8")
                                    photoURL = bundle.getString("photoURL").toString()
                                    //id = URLDecoder.decode(bundle.getString("id").toString(), "UTF-8")
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                MainImage(dp = 32.dp, photoURL) {}
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = fullname, fontSize = 16.sp)
                                    Text(text = statusUSER, fontSize = 14.sp)
                                }
                            }
                        } else {
                            Text(
                                text = currentRoute
                                    .toString()
                                    .replaceFirstChar { it.uppercase() })
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    actions = {//Элементы в конце TopAppBar
                        navController.addOnDestinationChangedListener { _, destination, _ -> //Хочу, чтобы выпадающий список появлялся только на странице настроек
                            checkButtonOnSettingsScreen(destination)
                            checkButtonOnChatScreen(destination)
                        }
                        if (flagDropMenuButtonOnSettingsScreen == 1) {
                            DropdownMenuItems(drawerState, coroutineScope, navController)
                        }
                    },
                    navigationIcon = {
                        navController.addOnDestinationChangedListener { _, destination, _ ->
                            checkButtonOnChatsScreen(destination)
                        }
                        if (flagNavButtonOnChatsScreen == 1) {
                            NavIconButton(coroutineScope, drawerState)
                        } else {
                            NavIconButton(coroutineScope, navController)
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

private fun checkButtonOnSettingsScreen(destination: NavDestination) {
    flagDropMenuButtonOnSettingsScreen =
        if ((destination.route == Screens.Settings.route)) {
            1
        } else {
            -1
        }
}


private fun checkButtonOnChatsScreen(destination: NavDestination) {
    flagNavButtonOnChatsScreen =
        if (destination.route == Screens.Chats.route) {
            1
        } else {
            -1
        }
}

private fun checkButtonOnChatScreen(destination: NavDestination) {
    flagNavButtonOnChatScreen =
        if (destination.route == "chatScreen/{fullname}/{status}/{photoURL}/{id}") {
            1
        } else {
            -1
        }
}


fun navButtonBack(navController: NavHostController) {
    navController.addOnDestinationChangedListener { _, destination, _ ->
        on_settings_screen = destination.route == Screens.ChangeName.route ||
                destination.route == Screens.ChangeUserName.route ||
                destination.route == Screens.ChangeBIO.route
    }

    if (on_settings_screen) {
        goTo(navController, Screens.Settings)
    } else {
        goTo(navController, Screens.Chats)
    }
}

@Composable
fun DropdownMenuItems(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    navController: NavHostController,
) {
    var expanded by remember { mutableStateOf(false) }

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
                    goTo(navController, Screens.ChangeName, coroutineScope, drawerState)
                },
                text = { Text("Изменить ФИО") }
            )
            DropdownMenuItem(
                onClick = {
                    goTo(navController, Screens.ChangePhotoUrl, coroutineScope, drawerState)
                },
                text = { Text("Изменить фото") }
            )
            HorizontalDivider()
            DropdownMenuItem(
                onClick = {
                    AppStatus.updateStates(AppStatus.OFFLINE, mainActivityContext)
                    get_out_from_auth = true
                    sign_in = true
                    AUTH.signOut()
                    goTo(LoginActivity::class.java, mainActivityContext)
                },
                text = { Text("Выйти из аккаунта") }
            )
        }
    }
}


