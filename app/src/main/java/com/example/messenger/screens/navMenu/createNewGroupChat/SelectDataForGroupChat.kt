package com.example.messenger.screens.navMenu.createNewGroupChat

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
import com.example.messenger.dataBase.firebaseFuns.createChatsListObj
import com.example.messenger.modals.ContactModal
import com.example.messenger.navigation.Screens
import com.example.messenger.screens.changeInfoScreens.pathToPhoto
import com.example.messenger.screens.componentOfScreens.ContactCard
import com.example.messenger.utils.Constants.FOLDER_PHOTOS
import com.example.messenger.utils.Constants.TYPE_GROUP
import com.example.messenger.utils.goTo
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.mainFieldStyle
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.whenSelect

@Composable
fun SelectDataForGroupChat(
    navController: NavHostController,
    contactList: MutableList<ContactModal>
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) } //Ссылка на картинку
    val bitmap = remember { mutableStateOf<Bitmap?>(null) } //Само изображение
    val launcher = //Открывает проводник для выбора картинки
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) // Проводник для выбора картинки
        { uri: Uri? ->
            imageUri = uri
        }

    val listState = rememberLazyListState()
    val contacts = remember { mutableStateListOf<ContactModal>().apply { addAll(contactList) } }

    val chatScreenState by remember {
        derivedStateOf { contacts }
    }

    val context = LocalContext.current
    val selectImage = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp)
                .clickable {
                    pathToPhoto = REF_STORAGE_ROOT
                        .child(FOLDER_PHOTOS)
                        .child(UID) //Получаем ссылку на корневую директори в БД
                    launcher.launch("image/*") //Открываем проводник для выбора картинки
                    selectImage.value = true
                }
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
//                    val infoArray = arrayOf(
//                        groupChatName,
//                        photoURL,
//                        receivingUserID,
//                        TYPE_GROUP,
//                        "lastMes_null",
//                        "timeStamp_null"
//                    )
//                    createGroupChat()
//                    createChatsListObj(infoArray)
//
//                    goTo(
//                        navController,
//                        Screens.GroupChat,
//                        contactList,
//                        groupChatName,
//                        "cb"
//                    )
                }
            ) {
                Text("Создать групповой чат")
            }
        }


    }
}

fun createGroupChat() {

}
