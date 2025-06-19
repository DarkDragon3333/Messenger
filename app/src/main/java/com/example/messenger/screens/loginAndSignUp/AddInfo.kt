package com.example.messenger.screens.loginAndSignUp

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messenger.MainActivity
import com.example.messenger.R
import com.example.messenger.dataBase.firebaseFuns.AUTH
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.dataBase.firebaseFuns.REF_STORAGE_ROOT
import com.example.messenger.dataBase.firebaseFuns.USER

import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.utils.Constants.CHILD_BIO
import com.example.messenger.utils.Constants.CHILD_CHAT_NAME
import com.example.messenger.utils.Constants.CHILD_ID
import com.example.messenger.utils.Constants.CHILD_PASSWORD
import com.example.messenger.utils.Constants.CHILD_PHONE
import com.example.messenger.utils.Constants.CHILD_PHOTO_URL
import com.example.messenger.utils.Constants.CHILD_STATUS
import com.example.messenger.utils.Constants.CHILD_USER_NAME
import com.example.messenger.utils.Constants.FOLDER_PHOTOS
import com.example.messenger.utils.Constants.NODE_PHONES
import com.example.messenger.utils.Constants.NODE_USERS
import com.example.messenger.utils.goTo
import com.example.messenger.utils.mainFieldStyle
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.sign_out
import androidx.core.net.toUri
import com.example.messenger.utils.pathToPhoto

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
            "android.resource://$packageName/${R.drawable.default_profile_image}".toUri() //Ссылка на фото по умолчанию
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
        var nameField by rememberSaveable { mutableStateOf("") }
        var surnameField by rememberSaveable { mutableStateOf("") }
        var userNameField by rememberSaveable { mutableStateOf("") }
        var bioField by rememberSaveable { mutableStateOf("") }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.padding(0.dp, 100.dp, 0.dp, 0.dp))
            Text(text = "Введите информацию о себе:")

            Spacer(modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp))
            mainFieldStyle(
                labelText = "Ваше имя",
                enable = true,
                1,
                nameField
            ) { name ->

            }

            Spacer(modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp))
            mainFieldStyle(
                labelText = "Ваша фамилия",
                enable = true,
                1,
                surnameField
            ) { surname ->
                surnameField = surname
            }

            Spacer(modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp))
            mainFieldStyle(
                labelText = "Ваш ник",
                enable = true,
                1,
                userNameField
            ) { userName ->
                surnameField = userName
            }

            Spacer(modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp))
            mainFieldStyle(
                labelText = "Немного о себе",
                enable = true,
                3,
                bioField
            ) { bio ->
                surnameField = bio
            }

            Spacer(modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
            Button(
                onClick = {
                    checkFields(nameField, userNameField, surnameField, bioField)
                }) {
                Icon(Icons.Default.Check, contentDescription = "")
                Text(text = "Подтвердить")
            }
        }
    }


    private fun checkFields(
        nameField: String,
        userNameField: String,
        surnameField: String,
        bioField: String
    ) {
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
    }

    private fun workWithDataForDataBase() {
        val uId = AUTH.currentUser?.uid.toString() //Берём Id текущего пользователя
        val dataMap = mutableMapOf<String, Any>() //Создаём место, куда погрузим наши данные для бд

        dataMap[CHILD_ID] = uId
        dataMap[CHILD_CHAT_NAME] = fullname
        dataMap[CHILD_USER_NAME] = userName
        dataMap[CHILD_BIO] = bio
        dataMap[CHILD_PHONE] = phoneNumber
        dataMap[CHILD_PASSWORD] = passwordFromSignUpActivity
        dataMap[CHILD_STATUS] = "В сети"
        sign_out = true

        REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uId)
            .addOnFailureListener { makeToast(it.message.toString(), context) }

        takeDefaultPhotoForUser(dataMap)
    }

    private fun takeDefaultPhotoForUser(dataMap: MutableMap<String, Any>) {
        pathToPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(dataMap[CHILD_ID].toString())
        pathToPhoto.putFile(uri).addOnCompleteListener { putTask ->
            when (putTask.isSuccessful) {
                true -> {
                    pathToPhoto.downloadUrl.addOnCompleteListener { downloadTask -> //Получаем ссылку на загруженную фотку
                        when (downloadTask.isSuccessful) {
                            true -> {
                                val photoURL = downloadTask.result.toString()
                                USER.photoUrl = photoURL
                                dataMap[CHILD_PHOTO_URL] = photoURL
                                updateFun(dataMap)
                            }

                            else -> makeToast(downloadTask.exception?.message.toString(), context)
                        }
                    }
                }

                else -> makeToast(putTask.exception?.message.toString(), context)
            }
        }
    }

    private fun updateFun(dataMap: MutableMap<String, Any>) {
        REF_DATABASE_ROOT.child(NODE_USERS).child(dataMap[CHILD_ID].toString())
            .updateChildren(dataMap) //Обращаемся по ссылке через бд к юзерам и сохраняем данные. Если юзеров нет, то firebase сам создаст каталог с юзерами, самого юзера по переданному Id и сохранит данные.
            .addOnCompleteListener {//Отправляем данные в базу данных файлом
                when (it.isSuccessful) {
                    true -> {
                        makeToast("Добро пожаловать!", context)
                        initUSER()
                        goTo(MainActivity::class.java, context)
                    }

                    false -> makeToast(it.exception?.message.toString(), context)
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




