package com.example.messenger.screens.navMenu.createNewGroupChat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.R
import com.example.messenger.dataBase.firebaseFuns.REF_STORAGE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.modals.ContactModal
import com.example.messenger.navigation.Screens
import com.example.messenger.screens.changeInfoScreens.pathToPhoto
import com.example.messenger.screens.componentOfScreens.ContactCard
import com.example.messenger.utils.Constants.CHILD_CONTACT_LIST
import com.example.messenger.utils.Constants.CHILD_GROUP_CHAT_NAME
import com.example.messenger.utils.Constants.CHILD_ID
import com.example.messenger.utils.Constants.CHILD_LAST_MESSAGE
import com.example.messenger.utils.Constants.CHILD_PHOTO_URL
import com.example.messenger.utils.Constants.CHILD_TIME_STAMP
import com.example.messenger.utils.Constants.CHILD_TYPE
import com.example.messenger.utils.Constants.FOLDER_PHOTOS
import com.example.messenger.utils.Constants.TYPE_GROUP
import com.example.messenger.utils.defaultImageUri
import com.example.messenger.utils.goTo
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.mainFieldStyle
import com.example.messenger.utils.makeToast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun SelectDataForGroupChat(
    navController: NavHostController,
    contactList: MutableList<ContactModal>
) {
    val mapInfo = hashMapOf<String, Any>()

    val context = LocalContext.current

    val selectImage = remember { mutableStateOf(false) }

    val groupChatId: String = createGroupChatId()
    mapInfo[CHILD_ID] = groupChatId

    var imageUri by remember { mutableStateOf<Uri?>(null) } //Ссылка на картинку
    val bitmap = remember { mutableStateOf<Bitmap?>(null) } //Само изображение
    val launcher = //Открывает проводник для выбора картинки
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) // Проводник для выбора картинки
        { uri: Uri? ->
            imageUri = uri
            selectYourImageForGroupChat(imageUri, mapInfo)
        }

    val listState = rememberLazyListState()
    val contacts = remember { mutableStateListOf<ContactModal>().apply { addAll(contactList) } }
    val chatScreenState by remember { derivedStateOf { contacts } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp)
                .clickable {
                    selectImageForGroupChat(launcher, selectImage)
                }
        ) {
            ControlOfChangeImageView(selectImage, imageUri, bitmap)
        }

        Spacer(modifier = Modifier.height(50.dp))

        val groupChatName = mainFieldStyle(
            labelText = "Введите название чата",
            enable = true,
            maxLine = 1
        ) {}

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
                        modifier = Modifier,
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        ContactCard(contact)
                        Row(
                            modifier = Modifier.clickable {
                                contacts.remove(contact)
                            }
                        ) {
                            Icon(
                                Icons.Filled.Clear,
                                ""
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Button(
                onClick = {
                    createGroupChat(
                        contactList,
                        mapInfo,
                        selectImage,
                        groupChatName,
                        imageUri,
                        context,
                        navController
                    )
                }
            ) {
                Text("Создать групповой чат")
            }
        }
    }
}

@Composable
private fun ControlOfChangeImageView(
    selectImage: MutableState<Boolean>,
    imageUri: Uri?,
    bitmap: MutableState<Bitmap?>
) {
    when (selectImage.value) {
        true -> imageUri?.let {
            val source = ImageDecoder.createSource(mainActivityContext.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)

            bitmap.value?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(192.dp)
                )
            }
        }

        false -> Image(
            painter = painterResource(R.drawable.def_image_msg),
            "",
            modifier = Modifier
                .clip(CircleShape)
                .size(192.dp)
        )
    }
}

private fun selectImageForGroupChat(
    launcher: ManagedActivityResultLauncher<String, Uri?>,
    selectImage: MutableState<Boolean>
) {
    pathToPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS)
        .child(UID) //Получаем ссылку на корневую директори в БД
    launcher.launch("image/*") //Открываем проводник для выбора картинки
    selectImage.value = true
}

private fun createGroupChat(
    contactList: MutableList<ContactModal>,
    mapInfo: HashMap<String, Any>,
    selectImage: MutableState<Boolean>,
    groupChatName: String,
    imageUri: Uri?,
    context: Context,
    navController: NavHostController
) {
    val contactListId = mutableListOf<String>()

    contactList.forEach { contact -> contactListId.add(contact.id) }.apply { USER.id }
    contactListId.add(USER.id)

    mapInfo[CHILD_GROUP_CHAT_NAME] = groupChatName
    mapInfo[CHILD_CONTACT_LIST] = contactListId
    mapInfo[CHILD_TYPE] = TYPE_GROUP
    mapInfo[CHILD_LAST_MESSAGE] = "lastMes_null"
    mapInfo[CHILD_TIME_STAMP] = "timeStamp_null"

    if (!selectImage.value)
        takeDefaultPhotoForGroupChat(mapInfo, mapInfo[CHILD_ID].toString(), contactListId, navController, contactList)
    else {
        addGroupChatToChatsList(mapInfo, contactListId, context) {
            makeToast("Чат создан", context)

            goTo(
                navController,
                Screens.GroupChat,
                contactList,
                groupChatName,
                imageUri.toString()
            )
        }
    }
}

fun createGroupChatId(): String {

    return "565"
}

private fun selectYourImageForGroupChat(imageUri: Uri?, dataMap: MutableMap<String, Any>) {
    imageUri.let { it -> //Получаем ссылку на картинку
        if (it != null) {
            pathToPhoto.putFile(it).addOnCompleteListener { //Загружаем картинку
                when (it.isSuccessful) { //Если загрузка прошла успешно
                    true -> {
                        pathToPhoto.downloadUrl.addOnCompleteListener { downloadTask -> //Получаем ссылку на загруженную фотку
                            when (downloadTask.isSuccessful) {
                                true -> {
                                    val photoURL = downloadTask.result.toString()
                                    dataMap[CHILD_PHOTO_URL] = photoURL
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

fun takeDefaultPhotoForGroupChat(
    dataMap: HashMap<String, Any>,
    groupChatId: String,
    contactListId: MutableList<String>,
    navController: NavHostController,
    contactList: MutableList<ContactModal>
) {
    pathToPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(groupChatId)
    pathToPhoto.putFile(defaultImageUri).addOnCompleteListener { putTask ->
        when (putTask.isSuccessful) {
            true -> {
                pathToPhoto.downloadUrl.addOnCompleteListener { downloadTask -> //Получаем ссылку на загруженную фотку
                    when (downloadTask.isSuccessful) {
                        true -> {
                            val photoURL = downloadTask.result.toString()
                            dataMap[CHILD_PHOTO_URL] = photoURL

                            addGroupChatToChatsList(dataMap, contactListId, mainActivityContext) {
                                makeToast("Чат создан", mainActivityContext)

                                goTo(
                                    navController,
                                    Screens.GroupChat,
                                    contactList,
                                    dataMap[CHILD_GROUP_CHAT_NAME].toString(),
                                    dataMap.toString()
                                )
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

fun addGroupChatToChatsList(
    mapInfo: HashMap<String, Any>,
    contactListId: MutableList<String>,
    context: Context,
    callBack: () -> Unit
) {
    try {
        contactListId.forEach { contactId ->
            val userLink =
                Firebase.firestore
                    .collection("users_talkers").document(contactId)
                    .collection("talkers").document(mapInfo[CHILD_ID].toString())

            userLink.set(mapInfo)
        }

        callBack()
    } catch (e: Exception) {
        Log.e("KotltalkApp", e.message.toString())
        makeToast(e.message.toString(), context)
    }
}