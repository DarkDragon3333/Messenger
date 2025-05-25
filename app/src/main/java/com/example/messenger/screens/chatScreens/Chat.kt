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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.R
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.utilsFilies.attachFile
import com.example.messenger.utilsFilies.attachImage
import com.example.messenger.dataBase.firebaseFuns.createChatsListObj
import com.example.messenger.dataBase.firebaseFuns.getMessageKey
import com.example.messenger.dataBase.firebaseFuns.initChat
import com.example.messenger.dataBase.firebaseFuns.listeningUpdateChat
import com.example.messenger.dataBase.firebaseFuns.uploadFileToStorage
import com.example.messenger.dataBase.valueEventListenerClasses.LastMessageState
import com.example.messenger.messageViews.sendText
import com.example.messenger.messageViews.startRecord
import com.example.messenger.messageViews.stopRecord
import com.example.messenger.modals.MessageModal
import com.example.messenger.screens.componentOfScreens.Message
import com.example.messenger.utilsFilies.voiceFiles.AppVoiceRecorder
import com.example.messenger.utilsFilies.Constants.TYPE_CHAT
import com.example.messenger.utilsFilies.Constants.TYPE_FILE
import com.example.messenger.utilsFilies.Constants.TYPE_IMAGE
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URLDecoder

lateinit var appVoiceRecorder: AppVoiceRecorder

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun ChatScreen(
    fullnameContact: String?,
    statusContact: String?,
    photoURLContact: String?,
    idContact: String,
    navController: NavHostController,
) {
    val fullname = URLDecoder.decode(fullnameContact, "UTF-8").toString()
    val statusUSER = URLDecoder.decode(statusContact, "UTF-8").toString()
    val photoURL = photoURLContact.toString()

    val regex = Regex("[{}]")
    val receivingUserID = idContact.replace(regex, "")

    val infoArray = arrayOf(
        fullname,
        photoURL,
        receivingUserID,
        statusUSER,
        TYPE_CHAT,
        "lastMes_null",
        "timeStamp_null"
    )

    var listenerRegistration: ListenerRegistration

    val viewConfiguration = LocalViewConfiguration.current

    val db = Firebase.firestore

    val recordVoiceFlag = remember { mutableStateOf(false) }
    val changeColor = remember { mutableStateOf(Color.Red) }

    val chatScreenState = remember { mutableStateListOf<MessageModal>() }

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
            .collection("messages").document(receivingUserID)
            .collection("TheirMessages")
            .orderBy("timeStamp", Query.Direction.DESCENDING) //Делает обратный порядок
            .limit(30)


    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uri: List<@JvmSuppressWildcards Uri> ->

        if (uri.isEmpty()) return@rememberLauncherForActivityResult

        val filesToUpload = uri.map { item ->
            val messageKey = getMessageKey(receivingUserID)
            messageKey to item
        }

        uploadFileToStorage(filesToUpload, receivingUserID, TYPE_IMAGE)
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uri: List<@JvmSuppressWildcards Uri> ->

        if (uri.isEmpty()) return@rememberLauncherForActivityResult

        val filesToUpload = uri.map { item ->
            val messageKey = getMessageKey(receivingUserID)
            messageKey to item
        }

        uploadFileToStorage(filesToUpload, receivingUserID, TYPE_FILE)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (isLoadingFirstMessages.value)
                Chat(listState, chatScreenState)
        }

        PanelOfEnter(
            text,
            interactionSource,
            changeColor,
            receivingUserID,
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
                    .document(receivingUserID)
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
                        chatScreenState.addAll(newMessages)

                        isLoadingOldMessages = false
                    }
                    .addOnFailureListener {
                        isLoadingOldMessages = false
                    }
            }
        }
    }

    DisposableEffect(Unit) {
        initChat(chatScreenState, messLink) { isLoadingFirstMessages.value = true }
        listenerRegistration = listeningUpdateChat(chatScreenState, messLink)

        onDispose {
            chatScreenState.clear()
            listenerRegistration.remove()
        }
    }

}

@Composable
private fun Chat(
    listState: LazyListState,
    chatScreenState: SnapshotStateList<MessageModal>,
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
    chatScreenState: SnapshotStateList<MessageModal>,
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
                        infoArray
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
fun AttachFileButton(
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    launcherFile: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>,
    showBottomSheetState: MutableState<Boolean>,
    coroutineScope: CoroutineScope,
    infoArray: Array<String>
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
                launcher, launcherFile, coroutineScope, sheetState, showBottomSheetState,
                infoArray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetContent(
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    launcherFile: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    showBottomSheetState: MutableState<Boolean>,
    infoArray: Array<String>
) {

    Row {
        Button(onClick = {
            attachImage(launcher)
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheetState.value = false
                }
            }
            createChatsListObj(infoArray)
        }) {
            Text("Image")
        }

        Button(onClick = {
            attachFile(launcherFile)
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheetState.value = false
                }
            }
            createChatsListObj(infoArray)
        }) {
            Text("File")
        }

        Button(onClick = {
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheetState.value = false
                }
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
    receivingUserID: String,
    recordVoiceFlag: MutableState<Boolean>,
    viewConfiguration: ViewConfiguration,
    chatScreenState: SnapshotStateList<MessageModal>,
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
        onClick = {

        }
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
                        startRecord(
                            changeColor,
                            receivingUserID,
                            recordVoiceFlag
                        )
                    }

                    is PressInteraction.Cancel -> {
                        if (isLongClick) {
                            stopRecord(
                                receivingUserID,
                                changeColor,
                                recordVoiceFlag
                            )
                            if (chatScreenState.size > 0)
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            createChatsListObj(infoArray)
                            LastMessageState.updateLastMessage("Голосовое сообщение", receivingUserID)
                        }
                    }

                    is PressInteraction.Release -> {
                        if (isLongClick) {
                            stopRecord(
                                receivingUserID,
                                changeColor,
                                recordVoiceFlag
                            )
                            if (chatScreenState.size > 0)
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            createChatsListObj(infoArray)
                        }

                        if (isLongClick.not()) {
                            if (fieldText.value.isNotEmpty()) {
                                sendText(
                                    fieldText.value,
                                    receivingUserID
                                )
                                if (chatScreenState.size > 0)
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                createChatsListObj(infoArray)
                                LastMessageState.updateLastMessage(fieldText.value, receivingUserID)
                                cleanText()
                            }

                        }
                    }
                }
            }
        }

        DisposableEffect(Unit) {
            appVoiceRecorder = AppVoiceRecorder()
            onDispose {
                appVoiceRecorder.releaseRecordedVoice()
            }
        }
    }
}

@Composable
private fun ControlIconOfVoiceButton(fieldText: MutableState<String>) {
    when (fieldText.value.isNotEmpty()) {
        true -> {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = ""
            )
        }

        false -> {
            Icon(
                painter = painterResource(id = R.drawable.ic_microphone),
                contentDescription = "",
            )
        }
    }
}