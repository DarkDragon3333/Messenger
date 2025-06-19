package com.example.messenger.screens.changeInfoScreens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.R
import com.example.messenger.dataBase.firebaseFuns.REF_STORAGE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.dataBase.firebaseFuns.choseChangeInformation
import com.example.messenger.utils.Constants.CHILD_PHOTO_URL
import com.example.messenger.utils.Constants.FOLDER_PHOTOS
import com.example.messenger.utils.UriImage
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.pathToPhoto
import com.google.firebase.storage.StorageReference

@Composable
fun ChangePhotoUrl(navController: NavHostController) {
    var imageUri by remember { mutableStateOf<Uri?>(null) } //Ссылка на картинку
    val bitmap = remember { mutableStateOf<Bitmap?>(null) } //Само изображение
    val launcher = //Открывает проводник для выбора картинки
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) // Проводник для выбора картинки
        { uri: Uri? ->
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GroupImagePreview(bitmap.value)

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                pathToPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS)
                    .child(UID) //Получаем ссылку на корневую директори в БД
                launcher.launch("image/*") //Открываем проводник для выбора картинки
            }
        ) { Text(text = "Pick Image") }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                choseNewPhoto(imageUri, navController)
            }
        )
        { Text(text = "Сохранить изменения") }
    }


}

@Composable
private fun GroupImagePreview(bitmap: Bitmap?) {
    if (bitmap != null) {
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
        UriImage(192.dp, USER.photoUrl) { }
    }
}

private fun choseNewPhoto(
    imageUri: Uri?,
    navController: NavHostController
) {
    if (imageUri != null) { //Если картинка выбрана
        imageUri.let { it -> //Получаем ссылку на картинку
            pathToPhoto.putFile(it).addOnCompleteListener { //Загружаем картинку
                when (it.isSuccessful) { //Если загрузка прошла успешно
                    true ->
                        choseChangeInformation(
                            "",
                            CHILD_PHOTO_URL,
                            mainActivityContext,
                            navController
                        )

                    else -> makeToast(it.exception?.message.toString(), mainActivityContext)
                }
            }
        }
    } else makeToast("Выберите изображение", mainActivityContext) //Если картинка не выбрана
}