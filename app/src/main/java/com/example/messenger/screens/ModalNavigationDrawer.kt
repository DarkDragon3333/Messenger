package com.example.messenger.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.dataBase.firebaseFuns.singOutFromApp
import com.example.messenger.navigation.DrawerNavigation
import com.example.messenger.navigation.Screens
import com.example.messenger.utils.NavIconButton
import com.example.messenger.utils.UriImage
import com.example.messenger.utils.goTo
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.on_settings_screen
import com.example.messenger.viewModals.ChatViewModal
import com.example.messenger.viewModals.ChatsViewModal
import com.example.messenger.viewModals.ContactsViewModal
import com.example.messenger.viewModals.CurrentChatHolderViewModal
import com.example.messenger.viewModals.GroupChatViewModal
import com.example.messenger.viewModals.NavDrawerViewModal
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavDrawer(
    currentChatHolderViewModal: CurrentChatHolderViewModal = viewModel(),
    navDrawerViewModal: NavDrawerViewModal = viewModel(),
    contactsViewModal: ContactsViewModal,
    chatsViewModal: ChatsViewModal = viewModel()
) {
    val navController = rememberNavController()
    val chatViewModal: ChatViewModal = viewModel()
    val groupChatViewModal: GroupChatViewModal = viewModel()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Screens.Chats

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

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

                navDrawerViewModal.getVisibleScreens()
                    .forEach { screen -> //Циклом генерируем элементы выдвигающегося меню
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
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Title(
                            currentRoute,
                            currentChatHolderViewModal,
                            navDrawerViewModal,
                            chatViewModal,
                            groupChatViewModal
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    actions = {//Элементы в конце TopAppBar
                        if (navDrawerViewModal.isSettings || navDrawerViewModal.isGroupChat)
                            DropdownMenuItems(
                                drawerState,
                                coroutineScope,
                                navController,
                                currentChatHolderViewModal
                            )
                    },
                    navigationIcon = {
                        when (navDrawerViewModal.isChats) {
                            true -> {
                                NavIconButton(coroutineScope, drawerState)
                                BackHandler {
                                    mainActivityContext.moveTaskToBack(true) // или exitProcess(0)
                                }
                            }

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
                DrawerNavigation(
                    navController,
                    currentChatHolderViewModal,
                    contactsViewModal,
                    chatsViewModal,
                    groupChatViewModal
                )
            }

            LaunchedEffect(Unit) {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    navDrawerViewModal.startListeningChangeDestination(destination.route)
                }
            }

            DisposableEffect(Unit) {

                onDispose {

                }
            }
        }
    }
}

@Composable
fun Title(
    currentRoute: Any,
    currentChatHolderViewModal: CurrentChatHolderViewModal,
    navDrawerViewModal: NavDrawerViewModal,
    chatViewModal: ChatViewModal,
    groupChatViewModal: GroupChatViewModal
) {
    if (navDrawerViewModal.isChat)
        ChatTitleView(currentChatHolderViewModal, chatViewModal)
    else if (navDrawerViewModal.isGroupChat)
        GroupChatTitleView(currentChatHolderViewModal, groupChatViewModal)
    else
        Text(
            text = currentRoute
                .toString()
                .replaceFirstChar { it.uppercase() })

}

@Composable
fun ChatTitleView(
    currentChatHolderViewModal: CurrentChatHolderViewModal = viewModel(),
    chatViewModal: ChatViewModal
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        UriImage(dp = 32.dp, chatViewModal.photoUrl.value) {}
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = chatViewModal.fullName.value, fontSize = 16.sp)
            if (chatViewModal.status.value.isNotBlank()) Text(text = chatViewModal.status.value, fontSize = 14.sp)
        }
    }
    LaunchedEffect(currentChatHolderViewModal.currentChat) {
        if (chatViewModal.status.value.isEmpty()){
            chatViewModal.initDataTitle(currentChatHolderViewModal.currentChat)
            chatViewModal.startListingChatTitle()
            chatViewModal.listingUsersStatus()
        }

    }
    DisposableEffect(Unit) {
        onDispose {
            chatViewModal.removeListener()
        }
    }
}

@Composable
fun GroupChatTitleView(
    currentChatHolderViewModal: CurrentChatHolderViewModal = viewModel(),
    groupChatViewModal: GroupChatViewModal,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UriImage(dp = 32.dp, groupChatViewModal.photoUrl.value) {}
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = groupChatViewModal.groupChatName.value, fontSize = 16.sp)
        }
    }
    LaunchedEffect(currentChatHolderViewModal.currentGroupChat) {
        if (groupChatViewModal.groupChatName.value.isEmpty()) {
            groupChatViewModal.initDataTitle(currentChatHolderViewModal.currentGroupChat)
            groupChatViewModal.startListingGroupChatTitle()
            groupChatViewModal.listingTitleChanges()
        }


        if (groupChatViewModal.getContactsData().isEmpty()){
            groupChatViewModal.downloadContactsData(
                currentChatHolderViewModal.currentGroupChat?.contactList ?: mutableListOf(),
                currentChatHolderViewModal.currentGroupChat?.id ?: ""
            )
        }

    }
    DisposableEffect(Unit) {

        onDispose {
            groupChatViewModal.removeListener()
        }
    }
}

@Composable
fun DropdownMenuItems(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    navController: NavHostController,
    currentChatHolderViewModal: CurrentChatHolderViewModal,
) {
    when (currentChatHolderViewModal.currentGroupChat != null) {
        true -> GroupChatDropMenu(navController)
        else -> SettingsDropMenu(drawerState, coroutineScope, navController)
    }

}

@Composable
fun GroupChatDropMenu(navController: NavHostController) {
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
                    goTo(navController, Screens.ChangeGroupChatData)
                },
                text = { Text("Найстройки чата") }
            )
            DropdownMenuItem(
                onClick = {

                },
                text = { Text("Настроить уведомления") }
            )
            HorizontalDivider()
            DropdownMenuItem(
                onClick = {

                },
                text = { Text("Выйти из группы") }
            )

        }
    }
}

@Composable
fun SettingsDropMenu(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    navController: NavHostController
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

