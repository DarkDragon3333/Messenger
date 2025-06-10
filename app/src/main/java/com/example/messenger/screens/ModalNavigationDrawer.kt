package com.example.messenger.screens

import android.os.Bundle
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.messenger.navigation.DrawerNavigation
import com.example.messenger.navigation.Screens
import com.example.messenger.utils.UriImage
import com.example.messenger.utils.NavIconButton
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.utils.flagDropMenuButtonOnSettingsScreen
import com.example.messenger.utils.flagNavButtonOnChatScreen
import com.example.messenger.utils.flagNavButtonOnChatsScreen
import com.example.messenger.utils.goTo
import com.example.messenger.utils.on_settings_screen
import com.example.messenger.dataBase.firebaseFuns.singOutFromApp
import com.example.messenger.utils.Constants.TYPE_CHAT
import com.example.messenger.utils.Constants.TYPE_GROUP
import com.example.messenger.utils.flagNavButtonOnGroupChatScreen
import com.example.messenger.viewModals.CurrentChatHolderViewModal
import kotlinx.coroutines.CoroutineScope
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavDrawer(
    currentChatHolderViewModal: CurrentChatHolderViewModal = viewModel()
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = currentBackStackEntry?.destination?.route ?: Screens.Chats

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val screens = listOf( //Созданные экраны в виде объектов
        Screens.YourProfile,
        Screens.Chats,
        Screens.SelectUsers,
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
                    UriImage(64.dp, USER.photoUrl) {
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

                screens.forEach { screen -> //Циклом генерируем элементы выдвигающегося меню
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
                                    goTo(
                                        navController,
                                        screen,
                                        coroutineScope,
                                        drawerState
                                    )
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
                        Title(navController, currentRoute, currentChatHolderViewModal)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    actions = {//Элементы в конце TopAppBar
                        navController.addOnDestinationChangedListener { _, destination, _ -> //Хочу, чтобы выпадающий список появлялся только на странице настроек
                            checkButtonOnSettingsScreen(destination)
                            checkButtonOnGroupChatScreen(destination)
                            checkButtonOnChatScreen(destination)
                        }
                        if (flagDropMenuButtonOnSettingsScreen == 1)
                            DropdownMenuItems(drawerState, coroutineScope, navController)
                    },
                    navigationIcon = {
                        navController.addOnDestinationChangedListener { _, destination, _ ->
                            checkButtonOnChatsScreen(destination)
                        }
                        when (flagNavButtonOnChatsScreen == 1) {
                            true -> NavIconButton(coroutineScope, drawerState)

                            false -> NavIconButton(coroutineScope, navController)
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
                DrawerNavigation(navController, currentChatHolderViewModal)
            }
        }
    }
}

@Composable
fun Title(
    navController: NavHostController,
    currentRoute: Any,
    currentChatHolderViewModal: CurrentChatHolderViewModal
) {
    if (flagNavButtonOnChatScreen == 1)
        TitleView(navController, TYPE_CHAT, currentChatHolderViewModal)
    else if (flagNavButtonOnGroupChatScreen == 1)
        TitleView(navController, TYPE_GROUP, currentChatHolderViewModal)
    else
        Text(
            text = currentRoute
                .toString()
                .replaceFirstChar { it.uppercase() })

}

@Composable
fun TitleView(
    navController: NavHostController,
    route: String,
    viewModel: CurrentChatHolderViewModal = viewModel()
) {
    val chat = viewModel.currentChat
    val group = viewModel.currentGroupChat

    var title =
        when {
            chat != null -> chat.fullname
            group != null -> group.groupChatName
            else -> ""
        }

    var statusUser = chat?.status ?: ""
    var photoURL = chat?.photoUrl ?: group?.photoUrl.orEmpty()

//    var groupChat: GroupChatModal
//    navController.addOnDestinationChangedListener { _, destination, bundle ->
//        if ((bundle != null) && (destination.route == route)) {
//            when (route) {
//                TYPE_CHAT -> {
//                    title = getBundle("fullname", bundle).toString()
//                    statusUSER = getBundle("status", bundle).toString()
//                    photoURL = bundle.getString("photoURL").toString()
//                    //id = URLDecoder.decode(bundle.getString("id").toString(), "UTF-8")
//                }
//
//                TYPE_GROUP -> {
//                    val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
//                    groupChat = savedStateHandle?.get<GroupChatModal>("groupChatModal")!!
//
//                    val f = savedStateHandle.get<String>("groupChatName")
//                    val p = savedStateHandle.get<String>("photoUrlGroupChat")
//
//                    title = f.toString()
//                    statusUSER = ""
//                    photoURL = p.toString()
//                }
//            }
//        }
//    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UriImage(dp = 32.dp, photoURL) {}
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 16.sp)
            if (statusUser.isNotBlank()) Text(text = statusUser, fontSize = 14.sp)
        }
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
                onClick = { singOutFromApp() },
                text = { Text("Выйти из аккаунта") }
            )
        }
    }
}

private fun checkButtonOnSettingsScreen(destination: NavDestination) {
    flagDropMenuButtonOnSettingsScreen =
        if (destination.route == Screens.Settings.route) 1 else -1
}

private fun checkButtonOnChatsScreen(destination: NavDestination) {
    flagNavButtonOnChatsScreen =
        if (destination.route == Screens.Chats.route) 1 else -1
}

private fun checkButtonOnGroupChatScreen(destination: NavDestination) {
    flagNavButtonOnGroupChatScreen =
        if (destination.route == Screens.GroupChat.route) 1 else -1
}

private fun checkButtonOnChatScreen(destination: NavDestination) {
    flagNavButtonOnChatScreen =
        if (destination.route == Screens.Chat.route) 1 else -1
}

fun navBackButton(navController: NavHostController) {
    navController.addOnDestinationChangedListener { _, destination, _ ->
        on_settings_screen =
            destination.route == Screens.ChangeName.route ||
                    destination.route == Screens.ChangeUserName.route ||
                    destination.route == Screens.ChangeBIO.route
    }

    when (on_settings_screen) {
        true -> goTo(navController, Screens.Settings)

        else -> goTo(navController, Screens.Chats)
    }
}

fun getBundle(info: String, bundle: Bundle): String? {
    return URLDecoder.decode(bundle.getString(info).toString(), "UTF-8")
}

