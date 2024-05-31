package com.example.messenger.user_sing_in_and_up_activities

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.MainActivity
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.utilsFilies.AUTH
import com.example.messenger.utilsFilies.goTo
import com.example.messenger.utilsFilies.initFirebase
import com.example.messenger.utilsFilies.mainFieldStyle
import com.example.messenger.utilsFilies.makeToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

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
                    GreetingInRegisterActivity(
                        modifier = Modifier.padding(innerPadding)
                    )
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
        initCallBack()
    }

    @Composable
    fun GreetingInRegisterActivity(modifier: Modifier = Modifier) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 100.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                val phoneField = mainFieldStyle(
                    labelText = "Номер телефона",
                    enable = true,
                    maxLine = 1
                ) {}

                Spacer(modifier = Modifier.padding(8.dp))
                val passwordField = mainFieldStyle(
                    labelText = "Пароль",
                    enable = true,
                    maxLine = 1
                ) {}

                Spacer(modifier = Modifier.padding(8.dp))
                val rePasswordField = mainFieldStyle(
                    labelText = "Повторите пароль",
                    enable = true,
                    maxLine = 1
                ) {}

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
                            authUser()
                        }
                    }
                ) {
                    Text("Sing up", fontSize = 18.sp)
                }
            }
        }
    }

    private fun authUser() {
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions
                .newBuilder(FirebaseAuth.getInstance())
                .setActivity(context)
                .setPhoneNumber(phoneNumberFromSignUp)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callBack)
                .build()
        )

    }

    //Прописываем варианты исхода аунтетификации
    private fun initCallBack() {
        callBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //Выполнение
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        makeToast("Добро пожаловать!", context)
                        goTo(MainActivity::class.java, context)
                    } else {
                        makeToast("Error!", context)
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


