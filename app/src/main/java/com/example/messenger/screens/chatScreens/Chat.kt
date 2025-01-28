package com.example.messenger.screens.chatScreens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
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
import com.example.messenger.dataBase.sendImageAsSMessage
import com.example.messenger.dataBase.sendMessage
import com.example.messenger.dataBase.valueEventListenerClasses.AppValueEventListener
import com.example.messenger.modals.CommonModal
import com.example.messenger.utilsFilies.cacheMessages
import com.example.messenger.utilsFilies.getCommonModel
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.net.URLDecoder

private lateinit var refToMessages: DatabaseReference
private lateinit var MessagesListener: AppValueEventListener
lateinit var pathToFile: StorageReference

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
    var messageKey = ""

    val regex = Regex("[{}]")
    val correctReceivingUserID = idContact.replace(regex, "")

    val chatScreenState = remember { mutableStateListOf<CommonModal>() }
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var fileUri by remember { mutableStateOf<Uri?>(null) } //Ссылка на картинку
    val bitmap = remember { mutableStateOf<Bitmap?>(null) } //Само изображение

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        fileUri = uri
        if (fileUri != null) {
            pathToFile.putFile(fileUri!!)
                .addOnSuccessListener {
                    val tempUri = REF_STORAGE_ROOT.child(FOLDER_MESSAGE_FILE).child(messageKey)
                    tempUri.downloadUrl.addOnCompleteListener { downloadTask ->
                        if (downloadTask.isSuccessful) {
                            val tempPhotoURL = downloadTask.result.toString()
                            sendImageAsSMessage(
                                correctReceivingUserID, tempPhotoURL, messageKey
                            )
                        } else {
                            makeToast(downloadTask.exception?.message.toString(), mainActivityContext)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    makeToast("Ошибка загрузки файла: ${exception.message}", mainActivityContext)
                }
        }
    }


    fun downloadImage(context: Context, navController: NavHostController) {
        //Загружаем фото пользователя
        val pathToPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(UID)
        pathToPhoto.downloadUrl.addOnCompleteListener { downloadTask -> //Получаем ссылку на загруженную фотку
            if (downloadTask.isSuccessful) {
                val tempPhotoURL = downloadTask.result.toString()
                changeInfo(tempPhotoURL, CHILD_PHOTO_URL, context, navController)
            } else {
                makeToast(downloadTask.exception?.message.toString(), context)
            }
        }

    }

    fun attachFile() {
        messageKey = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(correctReceivingUserID)
            .push().key.toString()
        pathToFile = REF_STORAGE_ROOT.child(FOLDER_MESSAGE_FILE).child(messageKey)

        launcher.launch(
            PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
            )
        )
    }

    fun initChat(id: String/*, count: Int*/) {
        refToMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)
        MessagesListener = AppValueEventListener { dataSnap ->
            cacheMessages = dataSnap.children.map { it.getCommonModel() }.toMutableList()
            if (chatScreenState.isNotEmpty()) {
                if (
                    (cacheMessages.last().timeStamp.toString().toLong() / 1000) !=
                    (chatScreenState.last().timeStamp.toString().toLong() / 1000)
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

        refToMessages.addValueEventListener(MessagesListener)

    }

    initChat(correctReceivingUserID)

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            contentAlignment = Alignment.TopStart
        ) {
            if (chatScreenState.size > 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = listState
                ) {
                    items(chatScreenState.size) { index ->
                        Message(chatScreenState[index])
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    coroutineScope.launch {
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
                        IconButton(onClick = {
                            attachFile()
                        }) {
                            Column {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_attach),
                                    contentDescription = ""
                                )
                            }
                        }
                        IconButton(onClick = {
                            val message = text.trim()
                            if (message.isEmpty()) {
                                makeToast("Введите сообщение", mainActivityContext)
                            } else {
                                sendMessage(message, correctReceivingUserID, TYPE_TEXT) {
                                    text = ""
                                    coroutineScope.launch() {
                                        listState.animateScrollToItem(chatScreenState.lastIndex)
                                    }
                                }
                            }
                        }) {
                            Column {
                                if (text.isEmpty()) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_microphone),
                                        contentDescription = ""
                                    )
                                } else {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Send,
                                        contentDescription = ""
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
        }
    }
}


fun isEditTagItemFullyVisible(lazyListState: LazyListState, editTagItemIndex: Int): Boolean {
    with(lazyListState.layoutInfo) {
        val editingTagItemVisibleInfo = visibleItemsInfo.find { it.index == editTagItemIndex }
        return if (editingTagItemVisibleInfo == null) {
            false
        } else {
            viewportEndOffset - editingTagItemVisibleInfo.offset >= editingTagItemVisibleInfo.size
        }
    }
}

