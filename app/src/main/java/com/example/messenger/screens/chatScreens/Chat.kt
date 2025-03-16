package com.example.messenger.screens.chatScreens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.R
import com.example.messenger.dataBase.CHILD_PHOTO_URL
import com.example.messenger.dataBase.FOLDER_MESSAGE_FILE
import com.example.messenger.dataBase.FOLDER_PHOTOS
import com.example.messenger.dataBase.NODE_MESSAGES
import com.example.messenger.dataBase.REF_DATABASE_ROOT
import com.example.messenger.dataBase.REF_STORAGE_ROOT
import com.example.messenger.dataBase.TYPE_TEXT
import com.example.messenger.dataBase.UID
import com.example.messenger.dataBase.changeInfo
import com.example.messenger.dataBase.getMessageKey
import com.example.messenger.dataBase.sendImageAsSMessage
import com.example.messenger.dataBase.sendMessage
import com.example.messenger.dataBase.valueEventListenerClasses.AppValueEventListener
import com.example.messenger.modals.CommonModal
import com.example.messenger.utilsFilies.AppVoiceRecorder
import com.example.messenger.utilsFilies.AppVoiceRecorder.Companion.startRecording
import com.example.messenger.utilsFilies.AppVoiceRecorder.Companion.stopRecording
import com.example.messenger.utilsFilies.RECORD_AUDIO
import com.example.messenger.utilsFilies.cacheMessages
import com.example.messenger.utilsFilies.getCommonModel
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.myCheckPermission
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder

private lateinit var refToMessages: DatabaseReference
private lateinit var MessagesListener: AppValueEventListener
lateinit var pathToFile: StorageReference

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
    val chatScreenState = remember { mutableStateListOf<CommonModal>() }
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var fileUri by remember { mutableStateOf<Uri?>(null) } //Ссылка на картинку
    val bitmap = remember { mutableStateOf<Bitmap?>(null) } //Само изображение

    val voiceMessage = remember { (MutableInteractionSource()) }
    val onPressVoiceButton by voiceMessage.collectIsPressedAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uri: List<@JvmSuppressWildcards Uri> ->

        if (uri.isEmpty()) return@rememberLauncherForActivityResult

        val filesToUpload = uri.map { item ->
            val messageKey = getMessageKey(receivingUserID)
            messageKey to item
        }

        //Переделка метода отправки сообщений. В теории, должно быть более оптимизированно
        CoroutineScope(Dispatchers.IO).launch {
            filesToUpload.forEach { (messageKey, fileUri) ->
                val tempUri = REF_STORAGE_ROOT.child(FOLDER_MESSAGE_FILE).child(messageKey)

                try {
                    tempUri.putFile(fileUri).await()
                    val downloadUrl = tempUri.downloadUrl.await().toString()
                    withContext(Dispatchers.Main) {
                        sendImageAsSMessage(receivingUserID, downloadUrl, messageKey)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        makeToast("Ошибка загрузки файла: ${e.message}", mainActivityContext)
                    }
                }
            }
        }
    }


//    fun downloadImage(context: Context, navController: NavHostController) {
//        //Загружаем фото пользователя
//        val pathToPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(UID)
//        pathToPhoto.downloadUrl.addOnCompleteListener { downloadTask -> //Получаем ссылку на загруженную фотку
//            if (downloadTask.isSuccessful) {
//                val tempPhotoURL = downloadTask.result.toString()
//                changeInfo(tempPhotoURL, CHILD_PHOTO_URL, context, navController)
//            } else {
//                makeToast(downloadTask.exception?.message.toString(), context)
//            }
//        }
//
//    }

    fun attachFile() {
        launcher.launch(
            PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
            )
        )
    }

    //Переделка метода инициализации класса. В теории, должно быть более оптимизированно
    fun initChat(id: String) {
        refToMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)
        MessagesListener = AppValueEventListener { dataSnap ->
            CoroutineScope(Dispatchers.IO).launch {
                val cacheMessages = dataSnap.children.map { it.getCommonModel() }.toMutableList()

                if (chatScreenState.isNotEmpty()) {
                    if ((cacheMessages.size) != //Проблема в том, что в casheMessanges у последего элемента почему-то другой timestamp
                        (chatScreenState.size) //Исправлено
                    ) {
                        chatScreenState.add(cacheMessages.last())
                        cacheMessages.clear()
                        coroutineScope.launch() {
                            listState.animateScrollToItem(chatScreenState.lastIndex)
                        }
                    }
                } else {
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
                        Message(message)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    coroutineScope.launch() {
                        if (chatScreenState.isNotEmpty())
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

                        Box(
                            modifier = Modifier
                                .size(50.dp, 65.dp)
                                .background(changeColor.value)
                                .pointerInput(Unit) { //Реагирую на то, что пользователь убрал палец с кнопки
                                    awaitPointerEventScope {
                                        while (true) {
                                            val upOrCancel = waitForUpOrCancellation()
                                            if (upOrCancel == null) {
                                                if (recordVoiceFlag.value) {
                                                    makeToast("Палец вверх", mainActivityContext)
                                                    stopRecording { file, messageKey ->
                                                        sendVoiceMessage(
                                                            Uri.fromFile(file),
                                                            messageKey
                                                        )
                                                    }
                                                    changeColor.value = Color.Red
                                                    recordVoiceFlag.value = false
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
                                                        makeToast(
                                                            "Запись идёт",
                                                            mainActivityContext
                                                        )
                                                        changeColor.value = Color.Blue
                                                        val messageKey =
                                                            getMessageKey(receivingUserID)
                                                        startRecording(messageKey)
                                                        recordVoiceFlag.value = true
                                                    }
                                                }

                                                false -> {
                                                    sendText(
                                                        text,
                                                        receivingUserID,
                                                        coroutineScope,
                                                        listState,
                                                        chatScreenState
                                                    )
                                                    text = ""
                                                }
                                            }
                                        },
                                        onTap = {
                                            if (text.trim().isNotEmpty()) {
                                                sendText(
                                                    text,
                                                    receivingUserID,
                                                    coroutineScope,
                                                    listState,
                                                    chatScreenState
                                                )
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
            AppVoiceRecorder.releaseRecordedVoice()
        }
    }

}


private fun sendText(
    text: String,
    receivingUserID: String,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    chatScreenState: SnapshotStateList<CommonModal>
): String {
    var text1 = text
    sendMessage(
        text1.trim(),
        receivingUserID,
        TYPE_TEXT
    ) {
        text1 = ""
        coroutineScope.launch {
            listState.animateScrollToItem(
                chatScreenState.lastIndex
            )
        }

    }
    return text1
}

fun sendVoiceMessage(uri: Uri, messageKey: String) {
    makeToast("Send Msg", mainActivityContext)


}

//fun changeIcon() {
//
//}


//fun isEditTagItemFullyVisible(lazyListState: LazyListState, editTagItemIndex: Int): Boolean {
//    with(lazyListState.layoutInfo) {
//        val editingTagItemVisibleInfo = visibleItemsInfo.find { it.index == editTagItemIndex }
//        return if (editingTagItemVisibleInfo == null) {
//            false
//        } else {
//            viewportEndOffset - editingTagItemVisibleInfo.offset >= editingTagItemVisibleInfo.size
//        }
//    }
//}

