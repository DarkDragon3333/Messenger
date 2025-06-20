package com.example.messenger.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
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

    val currentRBool = currentBackStackEntry?.destination?.route == Screens.Chats.route

    fun Color.adjustBrightness(factor: Float): Color {
        return Color(
            red = (red * factor).coerceIn(0f, 1f),
            green = (green * factor).coerceIn(0f, 1f),
            blue = (blue * factor).coerceIn(0f, 1f),
            alpha = alpha
        )
    }

    // Основной фон
    val mainBackground = MaterialTheme.colorScheme.onBackground
    // Фон меню — чуть темнее
    val drawerBackground = mainBackground.adjustBrightness(0.95f)
    // Фон топ-бара — чуть прозрачнее и чуть светлее основного
    val topBarBackground = mainBackground.copy(alpha = 0.85f)

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentRBool,
        drawerContent = {
            ModalDrawerSheet(
                windowInsets = WindowInsets(0), // Убираем отступы по умолчанию
                modifier = Modifier.background(MaterialTheme.colorScheme.background) // Прозрачный/основной фон
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                    UriImage(64.dp, USER.photoUrl) {
                        goTo(navController, Screens.YourProfile, coroutineScope, drawerState)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = USER.fullname, style = MaterialTheme.typography.bodyLarge)
                    Text(text = USER.phone, style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    navDrawerViewModal.getVisibleScreens().forEach { screen ->
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        if (navDrawerViewModal.isSettings || navDrawerViewModal.isGroupChat) {
                            DropdownMenuItems(
                                drawerState,
                                coroutineScope,
                                navController,
                                currentChatHolderViewModal
                            )
                        }
                    },
                    navigationIcon = {
                        if (navDrawerViewModal.isChats) {
                            NavIconButton(coroutineScope, drawerState)
                            BackHandler { mainActivityContext.moveTaskToBack(true) }
                        } else {
                            NavIconButton(coroutineScope, navController, navDrawerViewModal)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),

            ) {
                DrawerNavigation(
                    navController,
                    currentChatHolderViewModal,
                    contactsViewModal,
                    chatsViewModal,
                    groupChatViewModal,
                    chatViewModal
                )
            }

            LaunchedEffect(Unit) {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    navDrawerViewModal.startListeningChangeDestination(destination.route)
                }
            }

            DisposableEffect(Unit) {
                onDispose { /* тут можно добавить очистку */ }
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
            if (chatViewModal.status.value.isNotBlank()) Text(
                text = chatViewModal.status.value,
                fontSize = 14.sp
            )
        }
    }

    DisposableEffect(Unit) {
        chatViewModal.listingUsersData(currentChatHolderViewModal.currentChat?.id ?: " ")
        chatViewModal.startListingChatDataForTitle(currentChatHolderViewModal.currentChat?.id ?: " ")
        chatViewModal.initDataTitle(currentChatHolderViewModal.currentChat)

        onDispose {
            chatViewModal.removeListener()
            chatViewModal.removeDataTitle()
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

    DisposableEffect(Unit) {
        groupChatViewModal.initDataTitle(currentChatHolderViewModal.currentGroupChat)
        groupChatViewModal.startListingGroupChatData(currentChatHolderViewModal.currentGroupChat?.id
            ?: " ")
        groupChatViewModal.startListingGroupChatDataForTitle(currentChatHolderViewModal.currentGroupChat?.id
            ?: " ")
        if (groupChatViewModal.getContactsData().isEmpty()) {
            groupChatViewModal.downloadContactsData(
                currentChatHolderViewModal.currentGroupChat?.contactList ?: mutableListOf()
            )

        }
        onDispose {
            groupChatViewModal.removeListener()
            groupChatViewModal.removeDataTitle()
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

fun navBackButton(navController: NavHostController, navDrawerViewModal: NavDrawerViewModal) {

    if (navDrawerViewModal.isGroupChatSetting) {
        goTo(navController, Screens.GroupChat)
    } else if (navDrawerViewModal.isSelectUserForGroupChat) {
        goTo(navController, Screens.Chats)
    } else if (navDrawerViewModal.isSelectDataForGroupChat) {
        goTo(navController, Screens.SelectUsers)
    } else {
        goTo(navController, Screens.Chats)
    }

}

