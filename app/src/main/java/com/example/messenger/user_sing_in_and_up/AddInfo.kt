package com.example.messenger.user_sing_in_and_up

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
import com.example.messenger.user_sing_in_and_up.ui.theme.MessengerTheme
import com.example.messenger.utilis.AUTH
import com.example.messenger.utilis.CHILD_BIO
import com.example.messenger.utilis.CHILD_FULLNAME
import com.example.messenger.utilis.CHILD_ID
import com.example.messenger.utilis.CHILD_PASSWORD
import com.example.messenger.utilis.CHILD_PHONE
import com.example.messenger.utilis.CHILD_USER_NAME
import com.example.messenger.utilis.NODE_USERS
import com.example.messenger.utilis.REF_DATABASE_ROOT
import com.example.messenger.utilis.USER
import com.example.messenger.utilis.goTo
import com.example.messenger.utilis.mainFieldStyle
import com.example.messenger.utilis.makeToast

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
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

        dataForGetSignUpData = intent.extras ?: Bundle()
        verificationId =
            dataForGetSignUpData.getString("verificationId").toString() //Id пользователя
        passwordFromSignUpActivity = dataForGetSignUpData.getString("password").toString() //Пароль
        phoneNumber = dataForGetSignUpData.getString("phone").toString() //Номер телефона

        context = this
    }
    //TODO доделать ввод и передачу данных из AddActivity в MainActivity.

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.padding(0.dp, 100.dp, 0.dp, 0.dp))
            Text(text = "Введите информацию о себе:")

            Spacer(modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp))
            val nameField = mainFieldStyle(
                //rememberText = nameField,
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
                    fullname = nameField + surnameField
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

        REF_DATABASE_ROOT.child(NODE_USERS).child(uId)
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
        //USER.photoURL = photoURL
        USER.phone = phoneNumber
        USER.password = passwordFromSignUpActivity
    }

}




