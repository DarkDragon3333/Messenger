package com.example.messenger.screens.chatScreens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.messenger.messageView.sendText
import com.example.messenger.messageView.startRecord
import com.example.messenger.messageView.stopRecord
import com.example.messenger.modals.MessageModal
import com.example.messenger.utilsFilies.AppVoiceRecorder
import com.example.messenger.utilsFilies.RECORD_AUDIO
import com.example.messenger.utilsFilies.getMessageModel
import com.example.messenger.utilsFilies.myCheckPermission
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.net.URLDecoder

private lateinit var refToMessages: DatabaseReference
private lateinit var MessagesListener: AppValueEventListener
lateinit var pathToFile: StorageReference
lateinit var appVoiceRecorder: AppVoiceRecorder

@SuppressLint("ReturnFromAwaitPointerEventScope")
@OptIn(ExperimentalFoundationApi::class)
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
    var pressFlag = remember { mutableStateOf(false) }

    val regex = Regex("[{}]")
    val receivingUserID = idContact.replace(regex, "")

    val recordVoiceFlag = remember { mutableStateOf(false) }
    val changeColor = remember { mutableStateOf(Color.Red) }
    val chatScreenState = remember { mutableStateListOf<MessageModal>() }
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uri: List<@JvmSuppressWildcards Uri> ->

        if (uri.isEmpty()) return@rememberLauncherForActivityResult

        val filesToUpload = uri.map { item ->
            val messageKey = getMessageKey(receivingUserID)
            messageKey to item
        }

        //Переделка метода отправки сообщений. В теории, должно быть более оптимизированно
        uploadFileToStorage(filesToUpload, receivingUserID, TYPE_IMAGE)

    }

    appVoiceRecorder = AppVoiceRecorder()


    fun attachFile() {
        launcher.launch(
            PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
            )
        )
    }

    @Composable
    fun AttachFileButton() {
        IconButton(
            modifier = Modifier.size(50.dp, 65.dp),
            onClick = { attachFile() }
        ) {
            Column {
                Icon(
                    painter = painterResource(id = R.drawable.ic_attach),
                    contentDescription = ""
                )
            }
        }
    }

    //Переделка метода инициализации класса. В теории, должно быть более оптимизированно
    fun initChat(id: String) {
        refToMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)
        MessagesListener = AppValueEventListener { dataSnap ->
            val cacheMessages = dataSnap.children.map { it.getMessageModel() }.toMutableList()
            //Проблема в том, что в casheMessanges у последего элемента почему-то другой timestamp
            when (chatScreenState.isNotEmpty()) {
                true -> { //Исправлено
                    if ((cacheMessages.size) != (chatScreenState.size)) {
                        chatScreenState.add(cacheMessages.last())
                        cacheMessages.clear()
                        coroutineScope.launch() {
                            listState.animateScrollToItem(chatScreenState.lastIndex)
                        }
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

    initChat(receivingUserID)

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            contentAlignment = Alignment.TopStart
        ) {
            if (chatScreenState.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    items(chatScreenState) { message -> //Более читабельый код
                        Message(message, navController)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    coroutineScope.launch() {
                        listState.animateScrollToItem(chatScreenState.lastIndex)
                    }

                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(65.dp),
                placeholder = { Text(text = "Введите сообщение") },
                trailingIcon = {
                    Row {
                        AttachFileButton()

                        Box(
                            modifier = Modifier
                                .size(50.dp, 65.dp)
                                .background(changeColor.value)
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val upOrCancel = waitForUpOrCancellation() //Реагирую на то, что пользователь убрал палец с кнопки
                                            if (upOrCancel == null) {
                                                if (recordVoiceFlag.value) {
                                                    stopRecord(
                                                        receivingUserID,
                                                        changeColor,
                                                        recordVoiceFlag
                                                    )
                                                }

                                            }
                                        }
                                    }
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            when (text.trim().isEmpty()) {
                                                true -> {
                                                    if (myCheckPermission(RECORD_AUDIO)) {
                                                        startRecord(
                                                            changeColor,
                                                            receivingUserID,
                                                            recordVoiceFlag
                                                        )
                                                    }
                                                }

                                                false -> {
                                                    sendText(text, receivingUserID)
                                                    text = ""
                                                }
                                            }
                                        },
                                        onTap = {
                                            if (text.trim().isNotEmpty()) {
                                                sendText(text, receivingUserID)
                                                text = ""
                                            }
                                        },
                                    )
                                }

                        ) {
                            when (text.isEmpty()) {
                                true -> {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_microphone),
                                        contentDescription = "",
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }

                                false -> {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "",
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                },
            )
        }

    }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        if (destination.route != "chatScreen/{fullname}/{status}/{photoURL}/{id}") {
            refToMessages.removeEventListener(MessagesListener)
            appVoiceRecorder.releaseRecordedVoice()
        }
    }
}

