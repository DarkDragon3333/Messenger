package com.example.messenger.screens.chatScreens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.messenger.R
import com.example.messenger.dataBase.firebaseFuns.getMessageKey
import com.example.messenger.dataBase.firebaseFuns.uploadFileToStorage
import com.example.messenger.dataBase.valueEventListenerClasses.LastMessageState
import com.example.messenger.messageViews.sendTextToGroupChat
import com.example.messenger.messageViews.startRecordVoiceMsg
import com.example.messenger.messageViews.stopRecordVoiceMsg
import com.example.messenger.modals.MessageModal
import com.example.messenger.screens.componentOfScreens.Message
import com.example.messenger.utils.Constants.TYPE_FILE
import com.example.messenger.utils.Constants.TYPE_GROUP
import com.example.messenger.utils.Constants.TYPE_IMAGE
import com.example.messenger.utils.attachFile
import com.example.messenger.utils.attachImage
import com.example.messenger.utils.voice.AppVoiceRecorder
import com.example.messenger.viewModals.CurrentChatHolderViewModal
import com.example.messenger.viewModals.GroupChatViewModal
import com.example.messenger.viewModals.MessagesListViewModal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun GroupChat(
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal = viewModel()
) {
    val groupChatViewModal: GroupChatViewModal = viewModel()
    val messagesListViewModal: MessagesListViewModal = viewModel()

    val groupChatId =
        currentChatViewModel.currentGroupChat?.id?.replace(Regex("[{}]"), "").toString()

    val infoArray = arrayOf(
        currentChatViewModel.currentGroupChat?.chatName ?: "",
        currentChatViewModel.currentGroupChat?.photoUrl ?: "",
        groupChatId,
        TYPE_GROUP,
        "lastMes_null",
        "timeStamp_null",
    )

    //Исправить отображение кнопки записывания голосового сообщения
    val recordVoiceFlag = remember { mutableStateOf(false) }
    val changeColor = remember { mutableStateOf(Color.Red) }

    val chatScreenState = messagesListViewModal.getMessageList()

    val text = remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() } //Кнопка голосового сообщения
    val showBottomSheetState = remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uri: List<@JvmSuppressWildcards Uri> ->

        if (uri.isEmpty()) return@rememberLauncherForActivityResult

        val filesToUpload = uri.map { item ->
            val messageKey = getMessageKey(groupChatId)
            messageKey to item
        }

        uploadFileToStorage(
            filesToUpload = filesToUpload,
            receivingUserID = groupChatId,
            typeMessage = TYPE_IMAGE,
            typeChat = TYPE_GROUP,
            contactList = currentChatViewModel.currentGroupChat!!.contactList
        )
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uri: List<@JvmSuppressWildcards Uri> ->

        if (uri.isEmpty()) return@rememberLauncherForActivityResult

        val filesToUpload = uri.map { item ->
            val messageKey = getMessageKey(groupChatId)
            messageKey to item
        }

        uploadFileToStorage(
            filesToUpload = filesToUpload,
            receivingUserID = groupChatId,
            typeMessage = TYPE_FILE,
            typeChat = TYPE_GROUP,
            contactList = currentChatViewModel.currentGroupChat!!.contactList
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (messagesListViewModal.getFlagDownloadFirstMessages())
                Chat(listState, chatScreenState, groupChatViewModal)
        }

        PanelOfEnter(
            text,
            interactionSource,
            changeColor,
            groupChatId,
            recordVoiceFlag,
            LocalViewConfiguration.current,
            imageLauncher,
            fileLauncher,
            chatScreenState,
            coroutineScope,
            listState,
            showBottomSheetState,
            infoArray,
            currentChatViewModel
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.any { it.index == chatScreenState.lastIndex - 10 }
        }.collect { isVisible ->
            if (isVisible) messagesListViewModal.downloadOldMessages(groupChatId)
        }
    }

    DisposableEffect(Unit) {
        messagesListViewModal.initMessagesList(groupChatId) {
            messagesListViewModal.setFlagDownloadFirstMessages(
                true
            )
        }
        messagesListViewModal.startListingMessageList(groupChatId)

        if (currentChatViewModel.currentGroupChat?.contactList != null)
            groupChatViewModal.downloadContactsImages(currentChatViewModel.currentGroupChat!!.contactList)


        onDispose {
            messagesListViewModal.removeListener()
            currentChatViewModel.clearChat()
        }
    }

}

@Composable
private fun Chat(
    listState: LazyListState,
    chatScreenState: List<MessageModal>,
    groupChatViewModal: GroupChatViewModal
) {
    if (chatScreenState.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier,
            state = listState,
            reverseLayout = true
        ) {
            itemsIndexed(chatScreenState, key = { _, msg -> msg.id }) { index, message ->
                val nextMessage = chatScreenState.getOrNull(index + 1)
                val isFirstInChat = nextMessage?.from != message.from

                Message(
                    messageModal = message,
                    typeChat = TYPE_GROUP,
                    showAvatar = isFirstInChat,
                    avatarUrl = groupChatViewModal.getPhotoUrl(message.from).toString()
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun PanelOfEnter(
    text: MutableState<String>,
    interactionSource: MutableInteractionSource,
    changeColor: MutableState<Color>,
    receivingUserID: String,
    recordVoiceFlag: MutableState<Boolean>,
    viewConfiguration: ViewConfiguration,
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    launcherFile: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>,
    chatScreenState: List<MessageModal>,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    showBottomSheetState: MutableState<Boolean>,
    infoArray: Array<String>,
    currentChatViewModel: CurrentChatHolderViewModal
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(Color.Gray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text.value,
            onValueChange = { text.value = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color.Transparent)
                .verticalScroll(rememberScrollState()),
            placeholder = { Text(text = "Введите сообщение") },
            shape = RectangleShape,
            trailingIcon = {
                Row {
                    AttachFileButton(
                        currentChatViewModel,
                        launcher,
                        launcherFile,
                        showBottomSheetState,
                        coroutineScope,
                        infoArray,
                        receivingUserID
                    )
                }
            },
        )

        SendMessageButton(
            currentChatViewModel,
            interactionSource,
            fieldText = text,
            changeColor,
            receivingUserID,
            recordVoiceFlag,
            viewConfiguration,
            chatScreenState,
            coroutineScope,
            listState,
            infoArray,
        ) {
            text.value = ""
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachFileButton(
    currentChatHolderViewModal: CurrentChatHolderViewModal,
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    launcherFile: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>,
    showBottomSheetState: MutableState<Boolean>,
    coroutineScope: CoroutineScope,
    infoArray: Array<String>,
    receivingUserID: String
) {
    val sheetState = rememberModalBottomSheetState()

    IconButton(
        modifier = Modifier,
        onClick = { showBottomSheetState.value = true }
    ) {
        Column {
            Icon(
                painter = painterResource(id = R.drawable.ic_attach),
                contentDescription = ""
            )
        }
    }

    if (showBottomSheetState.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheetState.value = false },
            sheetState = sheetState
        ) {
            SheetContent(
                currentChatHolderViewModal,
                launcher, launcherFile, coroutineScope, sheetState, showBottomSheetState,
                receivingUserID
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetContent(
    currentChatHolderViewModal: CurrentChatHolderViewModal,
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    launcherFile: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    showBottomSheetState: MutableState<Boolean>,
    receivingUserID: String
) {
    Row {
        Button(onClick = {
            attachImage(launcher)
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible)
                    showBottomSheetState.value = false
            }

            LastMessageState.updateLastMessageInGroupChat(
                "Изображение", receivingUserID,
                currentChatHolderViewModal.currentGroupChat?.contactList ?: mutableListOf()
            )
        }) {
            Text("Image")
        }

        Button(onClick = {
            attachFile(launcherFile)
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible)
                    showBottomSheetState.value = false
            }
            LastMessageState.updateLastMessageInGroupChat(
                "Файл", receivingUserID,
                currentChatHolderViewModal.currentGroupChat?.contactList ?: mutableListOf()
            )
        }) {
            Text("File")
        }

        Button(onClick = {
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible)
                    showBottomSheetState.value = false
            }
        }) {
            Text("TO-DO")
        }
    }
}

@Composable
private fun SendMessageButton(
    currentChatHolderViewModal: CurrentChatHolderViewModal,
    interactionSource: MutableInteractionSource,
    fieldText: MutableState<String>,
    changeColor: MutableState<Color>,
    groupChatId: String,
    recordVoiceFlag: MutableState<Boolean>,
    viewConfiguration: ViewConfiguration,
    chatScreenState: List<MessageModal>,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    infoArray: Array<String>,
    cleanText: () -> Unit,
) {
    IconButton(
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.Transparent),
        onClick = {}
    ) {
        ControlIconOfVoiceButton(fieldText)

        LaunchedEffect(interactionSource) {
            var isLongClick = false

            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        isLongClick = false
                        delay(viewConfiguration.longPressTimeoutMillis)
                        isLongClick = true
                        appVoiceRecorder = AppVoiceRecorder()
                        startRecordVoiceMsg(
                            changeColor,
                            groupChatId,
                            recordVoiceFlag
                        )
                    }

                    is PressInteraction.Cancel -> {
                        if (isLongClick) {
                            stopRecordVoiceMsg(
                                groupChatId,
                                changeColor,
                                recordVoiceFlag,
                                TYPE_GROUP,
                                currentChatHolderViewModal.currentGroupChat!!.contactList
                            )
                            appVoiceRecorder.releaseRecordedVoice()
                            if (chatScreenState.isNotEmpty())
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            LastMessageState.updateLastMessageInGroupChat(
                                "Голосовое сообщение", infoArray[2].toString(),
                                currentChatHolderViewModal.currentGroupChat?.contactList
                                    ?: mutableListOf()
                            )
                        }
                    }

                    is PressInteraction.Release -> {
                        if (isLongClick) {
                            stopRecordVoiceMsg(
                                groupChatId,
                                changeColor,
                                recordVoiceFlag,
                                TYPE_GROUP,
                                currentChatHolderViewModal.currentGroupChat!!.contactList
                            )
                            appVoiceRecorder.releaseRecordedVoice()
                            if (chatScreenState.isNotEmpty())
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            LastMessageState.updateLastMessageInGroupChat(
                                "Голосовое сообщение", infoArray[2].toString(),
                                currentChatHolderViewModal.currentGroupChat?.contactList
                                    ?: mutableListOf()
                            )

                        }

                        if (isLongClick.not()) {
                            if (fieldText.value.isNotEmpty()) {
                                sendTextToGroupChat(
                                    fieldText.value,
                                    infoArray[2].toString(),
                                    currentChatHolderViewModal.currentGroupChat!!.contactList
                                )
                                if (chatScreenState.isNotEmpty())
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                LastMessageState.updateLastMessageInGroupChat(
                                    fieldText.value, infoArray[2].toString(),
                                    currentChatHolderViewModal.currentGroupChat?.contactList
                                        ?: mutableListOf()
                                )
                                cleanText()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlIconOfVoiceButton(fieldText: MutableState<String>) {
    when (fieldText.value.isNotEmpty()) {
        true ->
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = ""
            )

        false ->
            Icon(
                painter = painterResource(id = R.drawable.ic_microphone),
                contentDescription = "",
            )
    }
}
