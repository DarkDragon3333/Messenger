package com.example.messenger.user_sing_in_and_up

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.MainActivity
import com.example.messenger.changeNumberPhone.EnterCode
import com.example.messenger.user_sing_in_and_up.ui.theme.MessengerTheme
import com.example.messenger.utilis.AUTH
import com.example.messenger.utilis.goTo
import com.example.messenger.utilis.makeToast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        phoneNumberFromSignUp = ""
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
        initCallBack()
    }

    @Composable
    fun GreetingInRegisterActivity(modifier: Modifier = Modifier) {
        var phone by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rePassword by remember { mutableStateOf("") }

        Column {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 100.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                TextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("+7 880 555 55-55") }
                )
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Password") }
                )
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    value = rePassword,
                    onValueChange = { rePassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Repeat password") }
                )
                Spacer(modifier = Modifier.padding(120.dp))
                Button(
                    onClick = {
                        if (phone == ""){
                            makeToast("Введите номер телефона!", context)
                        } else if (password == ""){
                            makeToast("Придумайте пароль", context)
                        }
                        else if (rePassword != password){
                            makeToast("Проверьте повтореный пароль", context)
                        }
                        else{
                            phoneNumberFromSignUp = phone
                            authUser()
                        }
                    }
                ) {
                    Text("Sing up", fontSize = 18.sp)
                }
            }
        }
    }

    private fun authUser(){
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
    private fun initCallBack(){
        callBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //Выполнение
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener{
                    if (it.isSuccessful){
                        makeToast("Добро пожаловать!", context)
                        goTo(MainActivity::class.java, context)
                    }
                    else {
                        makeToast("Error!", context)
                    }
                }
            }
            //Ошибка
            override fun onVerificationFailed(e: FirebaseException) {
                makeToast(e.message.toString(), context)
            }
            //Отправка кода
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                goTo(EnterCode::class.java, context, verificationId, phoneNumberFromSignUp)
            }
        }
    }
}


