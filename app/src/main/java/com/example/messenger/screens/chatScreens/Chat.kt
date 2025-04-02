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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.messenger.dataBase.NODE_MESSAGES
import com.example.messenger.dataBase.REF_DATABASE_ROOT
import com.example.messenger.dataBase.TYPE_IMAGE
import com.example.messenger.dataBase.UID
import com.example.messenger.dataBase.getMessageKey
import com.example.messenger.dataBase.uploadFileToStorage
import com.example.messenger.dataBase.valueEventListenerClasses.AppValueEventListener
import com.example.messenger.messageViews.sendText
import com.example.messenger.messageViews.startRecord
import com.example.messenger.messageViews.stopRecord
import com.example.messenger.modals.MessageModal
import com.example.messenger.utilsFilies.AppVoiceRecorder
import com.example.messenger.utilsFilies.getMessageModel
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URLDecoder

private lateinit var refToMessages: DatabaseReference
private lateinit var MessagesListener: AppValueEventListener
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
    val fullname = URLDecoder.decode(fullnameContact, "UTF-8")
    val statusUSER = URLDecoder.decode(statusContact, "UTF-8")
    val photoURL = photoURLContact

    val regex = Regex("[{}]")
    val receivingUserID = idContact.replace(regex, "")

    val recordVoiceFlag = remember { mutableStateOf(false) }
    val changeColor = remember { mutableStateOf(Color.Red) }

    val chatScreenState = remember { mutableStateListOf<MessageModal>() }

    val text = remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() } //Кнопка голосового сообщения

    val viewConfiguration = LocalViewConfiguration.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uri: List<@JvmSuppressWildcards Uri> ->

        if (uri.isEmpty()) return@rememberLauncherForActivityResult

        val filesToUpload = uri.map { item ->
            val messageKey = getMessageKey(receivingUserID)
            messageKey to item
        }

        uploadFileToStorage(filesToUpload, receivingUserID, TYPE_IMAGE)

    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Chat(listState, chatScreenState)
        }

        PanelOfEnter(
            text,
            interactionSource,
            changeColor,
            receivingUserID,
            recordVoiceFlag,
            viewConfiguration,
            launcher
        )
    }

    LaunchedEffect(chatScreenState.size) {
        initChat(receivingUserID, chatScreenState)
        if (chatScreenState.size > 0) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatScreenState.lastIndex)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            chatScreenState.clear()
        }
    }
}

@Composable
private fun Chat(
    listState: LazyListState,
    chatScreenState: SnapshotStateList<MessageModal>
) {
    if (chatScreenState.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier,
            state = listState
        ) {
            items(
                chatScreenState, key = { it.id }
            ) { message ->
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
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>
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
                .background(Color.Transparent),
            placeholder = { Text(text = "Введите сообщение") },
            shape = RectangleShape,
            trailingIcon = {
                Row { AttachFileButton(launcher) }
            },
        )
        VoiceButton(
            interactionSource,
            fieldText = text,
            changeColor,
            receivingUserID,
            recordVoiceFlag,
            viewConfiguration
        ) {
            text.value = ""
        }
    }
}

@Composable
fun AttachFileButton(launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>) {
    IconButton(
        modifier = Modifier,
        onClick = { attachFile(launcher) }
    ) {
        Column {
            Icon(
                painter = painterResource(id = R.drawable.ic_attach),
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun VoiceButton(
    interactionSource: MutableInteractionSource,
    fieldText: MutableState<String>,
    changeColor: MutableState<Color>,
    receivingUserID: String,
    recordVoiceFlag: MutableState<Boolean>,
    viewConfiguration: ViewConfiguration,
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
                        }
                    }

                    is PressInteraction.Release -> {
                        if (isLongClick) {
                            stopRecord(
                                receivingUserID,
                                changeColor,
                                recordVoiceFlag
                            )
                        }

                        if (isLongClick.not()) {
                            if (fieldText.value.isNotEmpty()) {
                                sendText(
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
            appVoiceRecorder = AppVoiceRecorder()
            onDispose {
                refToMessages.removeEventListener(MessagesListener)
                appVoiceRecorder.releaseRecordedVoice()
            }
        }
    }
}

@Composable
private fun ControlIconOfVoiceButton(fieldText: MutableState<String>) {
    when (fieldText.value.isNotEmpty()) {
        false -> {
            Icon(
                painter = painterResource(id = R.drawable.ic_microphone),
                contentDescription = "",
            )
        }

        true -> {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = ""
            )
        }
    }
}

fun attachFile(launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>) {
    launcher.launch(
        PickVisualMediaRequest(
            mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
        )
    )
}

fun initChat(id: String, chatScreenState: SnapshotStateList<MessageModal>) {
    refToMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)

    if (::MessagesListener.isInitialized) {
        refToMessages.removeEventListener(MessagesListener)
    }

    MessagesListener = AppValueEventListener { dataSnap ->
        val cacheMessages = dataSnap.children.map { it.getMessageModel() }.toMutableList()
        when (chatScreenState.isNotEmpty()) {
            true -> {
                if ((cacheMessages.size) != (chatScreenState.size)) {
                    chatScreenState.add(cacheMessages.last())
                    cacheMessages.clear()
                }
            }

            false -> {
                chatScreenState.addAll(cacheMessages)
                cacheMessages.clear()
            }
        }
    }

    refToMessages.addValueEventListener(MessagesListener)
}