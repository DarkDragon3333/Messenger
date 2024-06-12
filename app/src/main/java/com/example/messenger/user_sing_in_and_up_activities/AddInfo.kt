package com.example.messenger.user_sing_in_and_up_activities

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messenger.MainActivity
import com.example.messenger.R
import com.example.messenger.screens.changeInfoScreens.pathToPhoto
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.dataBase.AUTH
import com.example.messenger.dataBase.CHILD_BIO
import com.example.messenger.dataBase.CHILD_FULLNAME
import com.example.messenger.dataBase.CHILD_ID
import com.example.messenger.dataBase.CHILD_PASSWORD
import com.example.messenger.dataBase.CHILD_PHONE
import com.example.messenger.dataBase.CHILD_PHOTO_URL
import com.example.messenger.dataBase.CHILD_STATUS
import com.example.messenger.dataBase.CHILD_USER_NAME
import com.example.messenger.dataBase.FOLDER_PHOTOS
import com.example.messenger.dataBase.NODE_PHONES
import com.example.messenger.dataBase.NODE_USERS
import com.example.messenger.dataBase.REF_DATABASE_ROOT
import com.example.messenger.dataBase.REF_STORAGE_ROOT
import com.example.messenger.dataBase.USER
import com.example.messenger.utilsFilies.goTo
import com.example.messenger.utilsFilies.mainFieldStyle
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.sign_out

class AddInfo : ComponentActivity() {
    private lateinit var bio: String
    private lateinit var status: String
    private lateinit var fullname: String
    private lateinit var photoURL: String
    private lateinit var userName: String
    private lateinit var verificationId: String //id пользователя
    private lateinit var dataForGetSignUpData: Bundle //Хранилище данных
    private lateinit var phoneNumber: String
    private lateinit var codeFromField: String
    lateinit var context: AddInfo
    private lateinit var passwordFromSignUpActivity: String
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GreetingAddInfo(
                        m = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun init() {
        bio = ""
        status = ""
        fullname = ""
        photoURL = ""
        userName = ""
        codeFromField = ""
        uri =
            Uri.parse("android.resource://$packageName/${R.drawable.default_image}") //Ссылка на фото по умолчанию
        dataForGetSignUpData = intent.extras ?: Bundle()
        verificationId =
            dataForGetSignUpData.getString("verificationId").toString() //Id пользователя
        passwordFromSignUpActivity =
            dataForGetSignUpData.getString("password").toString() //Пароль
        phoneNumber =
            dataForGetSignUpData.getString("phone").toString() //Номер телефона

        context = this
    }

    @Composable
    fun GreetingAddInfo(m: Modifier = Modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.padding(0.dp, 100.dp, 0.dp, 0.dp))
            Text(text = "Введите информацию о себе:")

            Spacer(modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp))
            val nameField = mainFieldStyle(
                labelText = "Ваше имя",
                enable = true,
                1
            ) {}

            Spacer(modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp))
            val surnameField = mainFieldStyle(
                labelText = "Ваша фамилия",
                enable = true,
                1
            ) {}

            Spacer(modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp))
            val userNameField = mainFieldStyle(
                labelText = "Ваш ник",
                enable = true,
                1
            ) {}

            Spacer(modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp))
            val bioField = mainFieldStyle(
                labelText = "Немного о себе",
                enable = true,
                3
            ) {}

            Spacer(modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
            Button(onClick = {
                if (nameField == "") {
                    makeToast("Введите имя в поле", context)
                } else if (userNameField == "") {
                    makeToast("Введите никнейм в поле", context)
                } else {
                    fullname = "$nameField $surnameField"
                    userName = userNameField
                    bio = bioField
                    workWithDataForDataBase()

                }
            }) {
                Icon(Icons.Default.Check, contentDescription = "")
                Text(text = "Подтвердить")
            }
        }
    }

    private fun workWithDataForDataBase() {
        val uId = AUTH.currentUser?.uid.toString() //Берём Id текущего пользователя
        val dataMap = mutableMapOf<String, Any>() //Создаём место, куда погрузим наши данные для бд

        dataMap[CHILD_ID] = uId
        dataMap[CHILD_FULLNAME] = fullname
        dataMap[CHILD_USER_NAME] = userName
        dataMap[CHILD_BIO] = bio
        dataMap[CHILD_PHONE] = phoneNumber
        dataMap[CHILD_PASSWORD] = passwordFromSignUpActivity
        dataMap[CHILD_STATUS] = "В сети"
        sign_out = true

        REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uId)
            .addOnFailureListener { makeToast(it.message.toString(), context) }

        takeDefaultPhoto(dataMap)
    }

    private fun takeDefaultPhoto(dataMap: MutableMap<String, Any>) {
        pathToPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(dataMap[CHILD_ID].toString())
        pathToPhoto.putFile(uri).addOnCompleteListener { putTask ->
            if (putTask.isSuccessful) {
                pathToPhoto.downloadUrl.addOnCompleteListener { downloadTask -> //Получаем ссылку на загруженную фотку
                    if (downloadTask.isSuccessful) {
                        val photoURL = downloadTask.result.toString()
                        USER.photoUrl = photoURL
                        dataMap[CHILD_PHOTO_URL] = photoURL
                        updateFun(dataMap)
                    } else {
                        makeToast(downloadTask.exception?.message.toString(), context)
                    }
                }
            } else {
                makeToast(putTask.exception?.message.toString(), context)
            }
        }
    }

    private fun updateFun(dataMap: MutableMap<String, Any>) {
        REF_DATABASE_ROOT.child(NODE_USERS).child(dataMap[CHILD_ID].toString())
            .updateChildren(dataMap) //Обращаемся по ссылке через бд к юзерам и сохраняем данные. Если юзеров нет, то firebase сам создаст каталог с юзерами, самого юзера по переданному Id и сохранит данные.
            .addOnCompleteListener {//Отправляем данные в базу данных файлом
                if (it.isSuccessful) {
                    makeToast("Добро пожаловать!", context)
                    initUSER()
                    goTo(MainActivity::class.java, context)
                } else {
                    makeToast(it.exception?.message.toString(), context)
                }
            }
    }

    private fun initUSER() {
        USER.id = verificationId
        USER.fullname = fullname
        USER.username = userName
        USER.bio = bio
        USER.status = "В сети"
        USER.phone = phoneNumber
        USER.password = passwordFromSignUpActivity
    }

}




