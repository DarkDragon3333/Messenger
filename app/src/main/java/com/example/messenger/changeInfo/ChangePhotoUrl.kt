package com.example.messenger.changeInfo

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.utilis.CHILD_PHOTO_URL
import com.example.messenger.utilis.FOLDER_PHOTOS
import com.example.messenger.utilis.REF_STORAGE_ROOT
import com.example.messenger.utilis.UID
import com.example.messenger.utilis.choseChangeInformation
import com.example.messenger.utilis.makeToast
import com.google.firebase.storage.StorageReference

lateinit var pathToPhoto: StorageReference

    @Composable
fun ChangePhotoUrl(navController: NavHostController) {
    val context = LocalContext.current //Активити, где мы находимся
    var imageUri by remember { mutableStateOf<Uri?>(null) } //Ссылка на картинку
    val bitmap = remember { mutableStateOf<Bitmap?>(null) } //Само изображение
    val launcher = //Открывает проводник для выбора картинки
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) // Проводник для выбора картинки
        { uri: Uri? ->
            imageUri = uri
        }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        imageUri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, it)
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

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                pathToPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(UID) //Получаем ссылку на корневую директори в БД
                launcher.launch("image/*") //Открываем проводник для выбора картинки
            }
        ) {
            Text(text = "Pick Image")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                if (imageUri!= null) { //Если картинка выбрана
                    imageUri?.let { it -> //Получаем ссылку на картинку
                        pathToPhoto.putFile(it).addOnCompleteListener { //Загружаем картинку
                            if (it.isSuccessful) { //Если загрузка прошла успешно
                                choseChangeInformation("", CHILD_PHOTO_URL, context, navController)
                            } else {
                                makeToast(it.exception?.message.toString(), context)
                            }
                        }
                    }
                }
                else{ //Если картинка не выбрана
                    makeToast("Выберите изображение", context)
                }

            }
        )
        { Text(text = "Сохранить изменения") }
    }


}