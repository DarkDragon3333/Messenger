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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.messenger.dataBase.firebaseFuns.REF_STORAGE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.dataBase.firebaseFuns.getMessageKey
import com.example.messenger.dataBase.firebaseFuns.initChat
import com.example.messenger.dataBase.firebaseFuns.listeningUpdateChat
import com.example.messenger.dataBase.firebaseFuns.uploadFileToStorage
import com.example.messenger.messageViews.sendTextToGroupChat
import com.example.messenger.messageViews.startRecordVoiceMsg
import com.example.messenger.messageViews.stopRecordVoiceMsg
import com.example.messenger.modals.MessageModal
import com.example.messenger.screens.componentOfScreens.Message
import com.example.messenger.utils.Constants.FOLDER_PHOTOS
import com.example.messenger.utils.Constants.TYPE_FILE
import com.example.messenger.utils.Constants.TYPE_GROUP
import com.example.messenger.utils.Constants.TYPE_IMAGE
import com.example.messenger.utils.UriImage
import com.example.messenger.utils.attachFile
import com.example.messenger.utils.attachImage
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.pathToSelectPhoto
import com.example.messenger.utils.voice.AppVoiceRecorder
import com.example.messenger.viewModals.CurrentChatHolderViewModal
import com.example.messenger.viewModals.GroupChatViewModal
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private lateinit var contactsList: MutableList<String>

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun GroupChat(
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal = viewModel()
) {
    val groupChatViewModal: GroupChatViewModal = viewModel()
    val groupChatId = currentChatViewModel.currentGroupChat?.id?.replace(Regex("[{}]"), "").toString()

    val infoArray = arrayOf(
        currentChatViewModel.currentGroupChat?.groupChatName ?: "",
        currentChatViewModel.currentGroupChat?.photoUrl ?: "",
        groupChatId,
        TYPE_GROUP,
        "lastMes_null",
        "timeStamp_null",
    )
    var listenerRegistration: ListenerRegistration

    val viewConfiguration = LocalViewConfiguration.current

    val db = Firebase.firestore

    //Исправить отображение кнопки записывания голосового сообщения
    val recordVoiceFlag = remember { mutableStateOf(false) }
    val changeColor = remember { mutableStateOf(Color.Red) }

    val messages = remember { mutableStateOf(listOf<MessageModal>()) }

    val chatScreenState by remember {
        derivedStateOf { messages.value }
    }

    val text = remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val isLoadingFirstMessages = remember { mutableStateOf(false) }
    var isLoadingOldMessages by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() } //Кнопка голосового сообщения

    val showBottomSheetState = remember { mutableStateOf(false) }

    val messLink =
        db
            .collection("users_messages").document(UID)
            .collection("messages").document(groupChatId)
            .collection("TheirMessages")
            .orderBy("timeStamp", Query.Direction.DESCENDING) //Делает обратный порядок
            .limit(30)

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
            contactList = contactsList
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
            contactList = contactsList
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (isLoadingFirstMessages.value)
                Chat(listState, chatScreenState, groupChatViewModal)
        }

        PanelOfEnter(
            text,
            interactionSource,
            changeColor,
            groupChatId,
            recordVoiceFlag,
            viewConfiguration,
            imageLauncher,
            fileLauncher,
            chatScreenState,
            coroutineScope,
            listState,
            showBottomSheetState,
            infoArray
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.any { it.index == chatScreenState.lastIndex - 10 }
        }.collect { isVisible ->
            if (isVisible && !isLoadingOldMessages && chatScreenState.isNotEmpty()) {
                isLoadingOldMessages = true

                val lastTimestamp = chatScreenState[chatScreenState.lastIndex].timeStamp
                db.collection("users_messages")
                    .document(UID)
                    .collection("messages")
                    .document(groupChatId)
                    .collection("TheirMessages")
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .startAfter(lastTimestamp)
                    .limit(30)
                    .get()
                    .addOnSuccessListener { result ->
                        val newMessages = result.documents.mapNotNull {
                            it.toObject(MessageModal::class.java)
                        }.filterNot { msg ->
                            chatScreenState.any { it.id == msg.id }
                        }
                        messages.value += newMessages
                        //chatScreenState.addAll(newMessages)

                        isLoadingOldMessages = false
                    }
                    .addOnFailureListener {
                        isLoadingOldMessages = false
                    }
            }
        }
    }

    DisposableEffect(Unit) {
        initChat(messages, messLink) { isLoadingFirstMessages.value = true }
        listenerRegistration = listeningUpdateChat(messages, messLink)

        contactsList = mutableListOf()

        if (currentChatViewModel.currentGroupChat?.contactList != null)
            contactsList.addAll(currentChatViewModel.currentGroupChat!!.contactList)

        downloadImages(groupChatViewModal)

        onDispose {
            messages.value = emptyList()
            listenerRegistration.remove()
            currentChatViewModel.clearChat()
        }
    }

}

fun DisposableEffectScope.downloadImages(groupChatViewModal: GroupChatViewModal) {
    contactsList.forEach { contactId ->
        pathToSelectPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(contactId)

        pathToSelectPhoto.downloadUrl.addOnCompleteListener { downloadTask ->
            when (downloadTask.isSuccessful) {
                true -> {
                    val photoURL = downloadTask.result.toString()
                    groupChatViewModal.setPhotoUrl(contactId, photoURL)
                }

                else -> makeToast(
                    downloadTask.exception?.message.toString(),
                    mainActivityContext
                )
            }
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
                val isFirstInGroup = nextMessage?.from != message.from

                Message(
                    messageModal = message,
                    typeChat = TYPE_GROUP,
                    showAvatar = isFirstInGroup,
                    avatarUrl = groupChatViewModal.getPhotoUrl(message.from).toString()
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
//            items(chatScreenState, key = { it.id }) { message ->
//                Message(messageModal = message, TYPE_GROUP, USER, groupChatViewModal)
//                Spacer(modifier = Modifier.height(10.dp))
//            }
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
    infoArray: Array<String>
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
            infoArray
        ) {
            text.value = ""
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachFileButton(
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
                launcher, launcherFile, coroutineScope, sheetState, showBottomSheetState,
                infoArray, receivingUserID
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetContent(
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    launcherFile: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    showBottomSheetState: MutableState<Boolean>,
    infoArray: Array<String>,
    receivingUserID: String
) {
    Row {
        Button(onClick = {
            attachImage(launcher)
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible)
                    showBottomSheetState.value = false
            }

            //LastMessageState.updateLastMessage("Изображение", receivingUserID)
        }) {
            Text("Image")
        }

        Button(onClick = {
            attachFile(launcherFile)
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible)
                    showBottomSheetState.value = false
            }
            //LastMessageState.updateLastMessage("Файл", receivingUserID)
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
    cleanText: () -> Unit
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
                                TYPE_GROUP
                            )
                            appVoiceRecorder.releaseRecordedVoice()
                            if (chatScreenState.isNotEmpty())
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            //addChatToChatsList(infoArray)
//                            LastMessageState.updateLastMessage(
//                                "Голосовое сообщение",
//                                groupChatId
//                            )
                        }
                    }

                    is PressInteraction.Release -> {
                        if (isLongClick) {
                            stopRecordVoiceMsg(
                                groupChatId,
                                changeColor,
                                recordVoiceFlag,
                                TYPE_GROUP
                            )
                            appVoiceRecorder.releaseRecordedVoice()
                            if (chatScreenState.isNotEmpty())
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
//                            LastMessageState.updateLastMessage(
//                                "Голосовое сообщение",
//                                groupChatId
//                            )
                            //addChatToChatsList(infoArray)
                        }

                        if (isLongClick.not()) {
                            if (fieldText.value.isNotEmpty()) {
                                sendTextToGroupChat(
                                    fieldText.value,
                                    infoArray[2].toString(),
                                    contactsList
                                )
                                if (chatScreenState.isNotEmpty())
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                //addChatToChatsList(infoArray)
                                //LastMessageState.updateLastMessage(fieldText.value, receivingUserID)
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
