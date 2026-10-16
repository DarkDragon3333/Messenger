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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.messenger.R
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.dataBase.firebaseFuns.addChatToChatsList
import com.example.messenger.dataBase.firebaseFuns.getMessageKey
import com.example.messenger.dataBase.firebaseFuns.uploadFileToStorage
import com.example.messenger.dataBase.valueEventListenerClasses.LastMessageState
import com.example.messenger.messageViews.sendText
import com.example.messenger.messageViews.startRecordVoiceMsg
import com.example.messenger.messageViews.stopRecordVoiceMsg
import com.example.messenger.modals.MessageModal
import com.example.messenger.modals.TokenModal
import com.example.messenger.screens.componentOfScreens.Message
import com.example.messenger.utils.Constants.TYPE_CHAT
import com.example.messenger.utils.Constants.TYPE_FILE
import com.example.messenger.utils.Constants.TYPE_IMAGE
import com.example.messenger.utils.attachFile
import com.example.messenger.utils.attachImage
import com.example.messenger.utils.voice.AppVoiceRecorder
import com.example.messenger.viewModals.CurrentChatHolderViewModal
import com.example.messenger.viewModals.MessagesListViewModal
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

lateinit var appVoiceRecorder: AppVoiceRecorder

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun ChatScreen(
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal,
    token: String,
    onRemoteTokenChange: (String) -> Unit,
    onSubmit: () -> Unit,
    messageText: String,
    onMessageChange: (String, String) -> Unit,
    onMessageSend: () -> Unit,
    onMessageBroadcast: () -> Unit
) {
    val messagesListViewModal: MessagesListViewModal = viewModel()

    val receivingUserID =
        currentChatViewModel.currentChat?.id?.replace(Regex("[{}]"), "").toString()

    val infoArray = arrayOf(
        currentChatViewModel.currentChat?.chatName.toString(),
        currentChatViewModel.currentChat?.photoUrl.toString(),
        receivingUserID,
        currentChatViewModel.currentChat?.status.toString(),
        TYPE_CHAT,
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
            val messageKey = getMessageKey(receivingUserID)
            messageKey to item
        }

        uploadFileToStorage(
            filesToUpload = filesToUpload,
            receivingUserID = receivingUserID,
            typeMessage = TYPE_IMAGE,
            typeChat = TYPE_CHAT
        )
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uri: List<@JvmSuppressWildcards Uri> ->

        if (uri.isEmpty()) return@rememberLauncherForActivityResult

        val filesToUpload = uri.map { item ->
            val messageKey = getMessageKey(receivingUserID)
            messageKey to item
        }

        uploadFileToStorage(
            filesToUpload = filesToUpload,
            receivingUserID = receivingUserID,
            typeMessage = TYPE_FILE,
            typeChat = TYPE_CHAT
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (messagesListViewModal.getFlagDownloadFirstMessages())
                Chat(listState, chatScreenState)
        }

        PanelOfEnter(
            text,
            interactionSource,
            changeColor,
            receivingUserID,
            recordVoiceFlag,
            LocalViewConfiguration.current,
            imageLauncher,
            fileLauncher,
            chatScreenState,
            coroutineScope,
            listState,
            showBottomSheetState,
            infoArray,
            token,
            onRemoteTokenChange,
            onSubmit,
            onMessageChange,
            onMessageSend

        )
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.any { it.index == chatScreenState.lastIndex - 10 }
        }.collect { isVisible ->
            if (isVisible) messagesListViewModal.downloadOldMessages(receivingUserID)
        }
    }

    DisposableEffect(Unit) {
        messagesListViewModal.initMessagesList(receivingUserID) {
            messagesListViewModal.setFlagDownloadFirstMessages(
                true
            )
        }
        messagesListViewModal.startListingMessageList(receivingUserID)

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
) {
    if (chatScreenState.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier,
            state = listState,
            reverseLayout = true
        ) {
            items(chatScreenState, key = { it.id }) { message ->
                Message(messageModal = message)
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
    token: String,
    onTokenChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onMessageChange: (String, String) -> Unit,
    onMessageSend: () -> Unit
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
            onValueChange = {
                text.value = it
            },
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
                        chatScreenState,
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
            token,
            onTokenChange,
            onSubmit,
            onMessageChange,
            onMessageSend
        ) {
            text.value = ""
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachFileButton(
    chatScreenState: List<MessageModal>,
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
            // Sheet content
            SheetContent(
                chatScreenState,
                launcher, launcherFile, coroutineScope, sheetState, showBottomSheetState,
                infoArray, receivingUserID
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetContent(
    chatScreenState: List<MessageModal>,
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    launcherFile: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    showBottomSheetState: MutableState<Boolean>,
    infoArray: Array<String>,
    receivingUserID: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = {
                attachImage(launcher)
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible)
                        showBottomSheetState.value = false
                }
                if (chatScreenState.isEmpty())
                    addChatToChatsList(infoArray)
                LastMessageState.updateLastMessageInChat("Изображение", receivingUserID)
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("Изображение")
        }

        Button(
            onClick = {
                attachFile(launcherFile)
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible)
                        showBottomSheetState.value = false
                }
                if (chatScreenState.isEmpty())
                    addChatToChatsList(infoArray)
                LastMessageState.updateLastMessageInChat("Файл", receivingUserID)
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("Файл")
        }
    }
}

@Composable
private fun SendMessageButton(
    interactionSource: MutableInteractionSource,
    fieldText: MutableState<String>,
    changeColor: MutableState<Color>,
    receivingUserID: String,
    recordVoiceFlag: MutableState<Boolean>,
    viewConfiguration: ViewConfiguration,
    chatScreenState: List<MessageModal>,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    infoArray: Array<String>,
    token: String,
    onTokenChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onMessageChange: (String, String) -> Unit,
    onMessageSend: () -> Unit,
    cleanText: () -> Unit
) {
    IconButton(
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.Transparent),
        onClick = {
        }
    ) {
        val context = LocalContext.current
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
                            context,
                            changeColor,
                            receivingUserID,
                            recordVoiceFlag,

                        )
                    }

                    is PressInteraction.Cancel -> {
                        if (isLongClick) {
                            stopRecordVoiceMsg(
                                receivingUserID,
                                changeColor,
                                recordVoiceFlag,
                                TYPE_CHAT
                            )
                            appVoiceRecorder.releaseRecorder()
                            if (chatScreenState.isNotEmpty())
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            if (chatScreenState.isEmpty())
                                addChatToChatsList(infoArray)
                            LastMessageState.updateLastMessageInChat(
                                "Голосовое сообщение",
                                receivingUserID
                            )
                        }
                    }

                    is PressInteraction.Release -> {
                        if (isLongClick) {
                            stopRecordVoiceMsg(
                                receivingUserID,
                                changeColor,
                                recordVoiceFlag,
                                TYPE_CHAT
                            )
                            appVoiceRecorder.releaseRecorder()
                            if (chatScreenState.isNotEmpty())
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            LastMessageState.updateLastMessageInChat(
                                "Голосовое сообщение",
                                receivingUserID
                            )
                            if (chatScreenState.isEmpty())
                                addChatToChatsList(infoArray)
                        }

                        if (isLongClick.not()) {
                            if (fieldText.value.isNotEmpty()) {
                                val tempText = fieldText.value.toString()
                                sendText(
                                    fieldText.value,
                                    receivingUserID
                                )

                                if (chatScreenState.isNotEmpty())

                                    coroutineScope.launch {

                                        listState.animateScrollToItem(0)

                                        val localToken = Firebase.messaging.token.await()
                                        val tempMap = mutableMapOf<String, String>()
                                        tempMap["token"] = localToken.toString()
                                        Firebase.firestore.collection("Tokens").document(UID).set(tempMap)

                                        var remoteToken = TokenModal()

                                        Firebase.firestore.collection("Tokens").document(receivingUserID).get().addOnCompleteListener { result ->
                                            if (result.isSuccessful){
                                                val task = result.result.toObject<TokenModal>(TokenModal::class.java)

                                                remoteToken = remoteToken.copy(
                                                    token = task?.token ?: "null"
                                                )
                                            }
                                        }.await()

                                        onTokenChange(remoteToken.token.toString())
                                        onSubmit()
                                        onMessageChange(tempText, infoArray[0].toString())
                                        onMessageSend()
                                    }

                                if (chatScreenState.isEmpty())
                                    addChatToChatsList(infoArray)

                                LastMessageState.updateLastMessageInChat(
                                    fieldText.value,
                                    receivingUserID
                                )

                                cleanText()
                            }
                        }
                    }
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {

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
