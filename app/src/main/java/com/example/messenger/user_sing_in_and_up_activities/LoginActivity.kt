package com.example.messenger.user_sing_in_and_up_activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.messenger.modals.User
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.utilsFilies.AUTH
import com.example.messenger.utilsFilies.NODE_PHONES
import com.example.messenger.utilsFilies.NODE_USERS
import com.example.messenger.utilsFilies.REF_DATABASE_ROOT
import com.example.messenger.utilsFilies.USER
import com.example.messenger.utilsFilies.authUser
import com.example.messenger.utilsFilies.goTo
import com.example.messenger.utilsFilies.initFirebase
import com.example.messenger.utilsFilies.initUser
import com.example.messenger.utilsFilies.mainFieldStyle
import com.example.messenger.utilsFilies.makeToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LoginActivity : ComponentActivity() {

    private lateinit var context: LoginActivity
    private lateinit var callBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    GreetingInLoginActivity(Modifier.padding(it))
                }
            }
        }
    }

    private fun init() {
        context = this
        initFirebase() //Инициализируем БД
        initUser(context)//Инициализируем пользователя
        if (AUTH.currentUser != null) { //Если пользователь уже есть
            goTo(MainActivity::class.java, context)
        }
    }

    @Composable
    fun GreetingInLoginActivity(m: Modifier = Modifier) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 100.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                val phone = mainFieldStyle(
                    labelText = "Номер телефона",
                    enable = true,
                    maxLine = 1
                ) {}
                Spacer(modifier = Modifier.padding(8.dp))
                val password = mainFieldStyle(
                    labelText = "Пароль",
                    enable = true,
                    maxLine = 1
                ) {}

                Spacer(modifier = Modifier.padding(120.dp))
                Button(
                    onClick = {
                        checkPhone(phone, password)
                    }
                ) { Text("Sing in", fontSize = 18.sp) }

                Box(
                    contentAlignment = Alignment.BottomCenter, modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp, 0.dp, 0.dp, 40.dp)
                ) { SingUpInLoginActivity() }

            }
        }

    }

    @Composable
    fun SingUpInLoginActivity() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "У вас нет аккаунта?\nДавайте зарегистрируемся!",
                modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp, 35.dp, 0.dp)
            ) {
                Button(
                    onClick = {
                        goTo(SingUpActivity::class.java, context)
                    }
                ) {
                    Text("Sing up", fontSize = 18.sp)
                }
            }
        }
    }

    private fun checkPhone(phone: String, password: String){
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                run breaking@ {
                    snapshot.children.forEach { snapshotPhone -> //Перебираем все номера телефонов
                        if (snapshotPhone.key == phone) { //Если номер телефона совпадает с введённым
                            downloadInfoOfUser(snapshotPhone, phone, password) //Получаем информацию о пользователе
                            return@breaking
                        }
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message, context)
            }

        })
    }

    fun downloadInfoOfUser(snapshotPhone: DataSnapshot, phone: String, password: String) {
        REF_DATABASE_ROOT.child(NODE_USERS).child(snapshotPhone.value.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshotUSER: DataSnapshot) {

                    USER = snapshotUSER.getValue(User::class.java) ?: User()
                    if (password == USER.password) { //Если пароль верный
                        initLoginCallBack(phone, password)  //Обработка запроса
                        authUser(context, phone, callBack) //Аунтетифицируем пользователя
                    } else {
                        makeToast("Неверный пароль!", context)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    makeToast(error.message, context)
                }

            })
    }

    //Прописываем варианты исхода аунтетификации
    private fun initLoginCallBack(phone: String, password: String) {
        callBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        makeToast("Добро пожаловать!", context)
                    } else {
                        makeToast("Error!", context)
                    }
                }
            }

            //Ошибка
            override fun onVerificationFailed(e: FirebaseException) {
                makeToast(e.message.toString(), context)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                goTo(
                    EnterCode::class.java,
                    context,
                    verificationId,
                    phone,
                    password
                )
            }
        }
    }

}

