package com.example.messenger.screens.loginAndSignUp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.sp
import com.example.messenger.MainActivity
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.dataBase.firebaseFuns.AUTH
import com.example.messenger.dataBase.firebaseFuns.authUser
import com.example.messenger.utils.goTo
import com.example.messenger.dataBase.firebaseFuns.initFirebase
import com.example.messenger.utils.MainFieldStyle
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.sign_in
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class SingUpActivity : ComponentActivity() {
    private lateinit var callBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks //Типы вызовов по время аунтетификации пользователя
    private lateinit var phoneNumberFromSignUp: String  //Вводимый номер телефона
    private lateinit var context: SingUpActivity //Переменная для отправки по приложению и отправки Toast
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
                setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    innerPadding
                    GreetingInRegisterActivity()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init() {
        context = this
        phoneNumberFromSignUp = ""
        password = ""
        initFirebase()
        initSingUpCallBack()
    }

    @Composable
    fun GreetingInRegisterActivity() {
        var phoneField by rememberSaveable { mutableStateOf("") }
        var passwordField by rememberSaveable { mutableStateOf("") }
        var rePasswordField by rememberSaveable { mutableStateOf("") }
            Column {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 100.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                MainFieldStyle(
                    labelText = "Номер телефона",
                    enable = true,
                    maxLine = 1,
                    phoneField
                ) { phone ->
                    phoneField = phone
                }

                Spacer(modifier = Modifier.padding(8.dp))
                MainFieldStyle(
                    labelText = "Пароль",
                    enable = true,
                    maxLine = 1,
                    passwordField
                ) { password ->
                    passwordField = password

                }

                Spacer(modifier = Modifier.padding(8.dp))
                MainFieldStyle(
                    labelText = "Повторите пароль",
                    enable = true,
                    maxLine = 1,
                    rePasswordField
                ) { rePassword ->
                    rePasswordField = rePassword

                }

                Spacer(modifier = Modifier.padding(120.dp))
                Button(
                    onClick = {
                        if (phoneField == "") {
                            makeToast("Введите номер телефона!", context)
                        } else if (passwordField == "") {
                            makeToast("Придумайте пароль", context)
                        } else if (rePasswordField != passwordField) {
                            makeToast("Проверьте повтореный пароль", context)
                        } else {
                            phoneNumberFromSignUp = phoneField
                            password = passwordField
                            sign_in = false
                            authUser(context, phoneNumberFromSignUp, callBack)

                        }
                    }
                ) {
                    Text("Sing up", fontSize = 18.sp)
                }
            }
        }
    }

    //Прописываем варианты исхода аунтетификации
    private fun initSingUpCallBack() {
        callBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //Выполнение
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener {
                    when (it.isSuccessful) {
                        true -> {
                            makeToast("Добро пожаловать!", context)
                            goTo(MainActivity::class.java, context)
                        }
                        false -> makeToast("Error!", context)
                    }
                }
            }

            //Ошибка
            override fun onVerificationFailed(e: FirebaseException) {
                makeToast(e.message.toString(), context)
            }

            //Отправка кода
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                goTo(
                    EnterCode::class.java,
                    context,
                    verificationId,
                    phoneNumberFromSignUp,
                    password
                )
            }
        }
    }
}


