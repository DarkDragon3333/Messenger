package com.example.messenger.screens.navMenu.createNewGroupChat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.R
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.dataBase.firebaseFuns.REF_STORAGE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.dataBase.firebaseFuns.addGroupChatToChatsList
import com.example.messenger.modals.ContactModal
import com.example.messenger.modals.GroupChatModal
import com.example.messenger.modals.User
import com.example.messenger.navigation.Screens
import com.example.messenger.screens.componentOfScreens.AddContactCard
import com.example.messenger.utils.Constants.ADMINISTRATOR
import com.example.messenger.utils.Constants.CHILD_CHAT_NAME
import com.example.messenger.utils.Constants.CHILD_CONTACT_LIST
import com.example.messenger.utils.Constants.CHILD_ID
import com.example.messenger.utils.Constants.CHILD_LAST_MESSAGE
import com.example.messenger.utils.Constants.CHILD_PHOTO_URL
import com.example.messenger.utils.Constants.CHILD_TYPE
import com.example.messenger.utils.Constants.FOLDER_PHOTOS
import com.example.messenger.utils.Constants.NODE_USERS
import com.example.messenger.utils.Constants.TYPE_GROUP
import com.example.messenger.utils.defaultImageUri
import com.example.messenger.utils.goTo
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.pathToSelectPhoto
import com.example.messenger.viewModals.CurrentChatHolderViewModal
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

@Composable
fun SelectDataForGroupChat(
    navController: NavHostController,
    contactsList: MutableList<ContactModal>,
    currentChatViewModel: CurrentChatHolderViewModal,
) {
    // Информация для группового чата
    val mapInfo = remember { hashMapOf<String, Any>() }

    // Уникальный ID чата
    val groupChatId = remember { mutableStateOf(createGroupChatId()) }
    mapInfo[CHILD_ID] = groupChatId.value

    // URI и Bitmap картинки
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    // Выбор изображения через проводник
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            uri?.let {
                try {
                    val source = ImageDecoder.createSource(mainActivityContext.contentResolver, it)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    val listState = rememberLazyListState()
    val contacts = remember { mutableStateListOf<ContactModal>().apply { addAll(contactsList) } }
    val chatScreenState by remember { derivedStateOf { contacts } }

    var groupChatName by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        // Кнопка выбора изображения
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp)
                .clickable {
                    launcher.launch("image/*")
                }
        ) {
            GroupImagePreview(bitmap.value)
        }

        Spacer(modifier = Modifier.height(50.dp))


        OutlinedTextField(
            value = groupChatName,
            onValueChange = { groupChatName = it },
            label = { Text("Введите название чата") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
            ) {
                items(chatScreenState, key = { it.id }) { contact ->
                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AddContactCard(contact)
                        Row(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable {
                                    if (contacts.size > 1) {
                                        contacts.remove(contact)
                                    } else {
                                        makeToast("Слишком мало участников", mainActivityContext)
                                    }
                                }
                        ) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Удалить",
                                tint = Color.Red
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Button(
                onClick = {
                    if (contacts.isEmpty() || groupChatName.isBlank()) {
                        makeToast("Добавьте участников и имя", mainActivityContext)
                    } else {
                        createGroupChat(
                            contacts,
                            mapInfo,
                            bitmap, // временно передаём true
                            groupChatName,
                            imageUri,
                            mainActivityContext,
                            navController,
                            currentChatViewModel
                        )
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Создать групповой чат")
            }
        }
    }
}


@Composable
private fun GroupImagePreview(bitmap: Bitmap?) {
    if (bitmap != null && bitmap.width > 10 && bitmap.height > 10) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier
                .size(192.dp)
                .clip(CircleShape)
        )
    } else {
        Image(
            painter = painterResource(R.drawable.default_profile_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier
                .size(192.dp)
                .clip(CircleShape)
        )
    }
}


private fun createGroupChat(
    contactList: MutableList<ContactModal>,
    mapInfo: HashMap<String, Any>,
    selectImage: MutableState<Bitmap?>,
    groupChatName: String,
    imageUri: Uri?,
    context: Context,
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal
) {
    val contactListId = mutableListOf<String>()

    contactList.forEach { contact ->
        contactListId.add(contact.id)
    }
    contactListId.add(USER.id)

    mapInfo[CHILD_CHAT_NAME] = groupChatName
    mapInfo[CHILD_CONTACT_LIST] = contactListId
    mapInfo[CHILD_TYPE] = TYPE_GROUP
    mapInfo[CHILD_LAST_MESSAGE] = "Чат создан"
    mapInfo["timeStamp"] = FieldValue.serverTimestamp()
    mapInfo[ADMINISTRATOR] = UID

    if (selectImage.value == null)
        takeDefaultPhotoForGroupChat(
            mapInfo,
            mapInfo[CHILD_ID].toString(),
            contactListId,
            navController,
            currentChatViewModel
        )
    else {
        pathToSelectPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(mapInfo[CHILD_ID].toString())
        getYourImageForGroupChat(imageUri, mapInfo) {
            addGroupChatToChatsList(mapInfo, contactListId, context) { timeStamp ->
                makeToast("Чат создан", mainActivityContext)

                    val groupChatModel = GroupChatModal(
                        mapInfo[CHILD_CHAT_NAME].toString(),
                        imageUri.toString(),
                        mapInfo[CHILD_ID].toString(),
                        "",
                        mapInfo[CHILD_CONTACT_LIST] as MutableList<String>,
                        USER.id,
                        mapInfo[CHILD_TYPE].toString(),
                        mapInfo[CHILD_LAST_MESSAGE].toString(),
                        timeStamp
                    )
                    currentChatViewModel.setGroupChat(groupChatModel)
                    goTo(navController, Screens.GroupChat)

            }
        }
    }
}


fun createGroupChatId(): String {
    val groupId = Firebase.firestore.collection("users_groups").document().id

    return groupId
}

fun takeDefaultPhotoForGroupChat(
    mapInfo: HashMap<String, Any>,
    groupChatId: String,
    contactListId: MutableList<String>,
    navController: NavHostController,
    currentChatViewModel: CurrentChatHolderViewModal
) {
    pathToSelectPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(groupChatId)
    pathToSelectPhoto.putFile(defaultImageUri).addOnCompleteListener { putTask ->
        when (putTask.isSuccessful) {
            true -> {
                pathToSelectPhoto.downloadUrl.addOnCompleteListener { downloadTask -> //Получаем ссылку на загруженную фотку
                    when (downloadTask.isSuccessful) {
                        true -> {
                            val photoURL = downloadTask.result.toString()
                            mapInfo[CHILD_PHOTO_URL] = photoURL

                            addGroupChatToChatsList(
                                mapInfo,
                                contactListId,
                                mainActivityContext
                            ) { timeStamp ->
                                makeToast("Чат создан", mainActivityContext)
                                    val groupChatModel = GroupChatModal(
                                        mapInfo[CHILD_CHAT_NAME].toString(),
                                        mapInfo[CHILD_PHOTO_URL].toString(),
                                        mapInfo[CHILD_ID].toString(),
                                        "",
                                        mapInfo[CHILD_CONTACT_LIST] as MutableList<String>,
                                        USER.id,
                                        mapInfo[CHILD_TYPE].toString(),
                                        mapInfo[CHILD_LAST_MESSAGE].toString(),
                                        timeStamp
                                    )
                                    currentChatViewModel.setGroupChat(groupChatModel)
                                    goTo(navController, Screens.GroupChat)
                            }
                        }

                        else -> makeToast(
                            downloadTask.exception?.message.toString(),
                            mainActivityContext
                        )
                    }
                }
            }

            else -> makeToast(putTask.exception?.message.toString(), mainActivityContext)
        }
    }
}

fun getYourImageForGroupChat(
    imageUri: Uri?,
    mapInfo: MutableMap<String, Any>,
    function: () -> Unit
) {
    imageUri.let { it -> //Получаем ссылку на картинку
        if (it != null) {
            pathToSelectPhoto.putFile(it).addOnCompleteListener { //Загружаем картинку
                when (it.isSuccessful) { //Если загрузка прошла успешно
                    true -> {
                        pathToSelectPhoto.downloadUrl.addOnCompleteListener { downloadTask -> //Получаем ссылку на загруженную фотку
                            when (downloadTask.isSuccessful) {
                                true -> {
                                    val photoURL = downloadTask.result.toString()
                                    mapInfo[CHILD_PHOTO_URL] = photoURL
                                    function()
                                }

                                else -> makeToast(
                                    downloadTask.exception?.message.toString(),
                                    mainActivityContext
                                )
                            }
                        }
                        makeToast("Изображение загружено успешно", mainActivityContext)
                    }

                    else -> makeToast(it.exception?.message.toString(), mainActivityContext)
                }
            }
        }
    }
}