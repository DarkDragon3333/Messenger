package com.example.messenger.screens.loginAndSignUp

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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.MainActivity
import com.example.messenger.dataBase.firebaseFuns.AUTH
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.dataBase.firebaseFuns.authUser
import com.example.messenger.dataBase.firebaseFuns.initFirebase
import com.example.messenger.modals.User
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.utils.Constants.NODE_PHONES
import com.example.messenger.utils.Constants.NODE_USERS
import com.example.messenger.utils.goTo
import com.example.messenger.utils.MainFieldStyle
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.whenSelect
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
            InitUserLoginActivity() //Инициализируем пользователя
        }
    }

    private fun init() {
        context = this
        initFirebase() //Инициализируем БД
    }

    @Composable
    private fun InitUserLoginActivity() {
        REF_DATABASE_ROOT
            .child(NODE_USERS)
            .child(UID)
            .addListenerForSingleValueEvent(
                object : ValueEventListener { //Один раз при запуске обновляем наши данные
                    override fun onDataChange(snapshot: DataSnapshot) {
                        USER = snapshot.getValue(User::class.java)
                            ?: User() //Получаем данные через переменную snapshot. Если будет null поле, то вы инициализируем пустым пользователем

                        when (AUTH.currentUser != null) { //Если пользователь уже есть
                            true -> goTo(MainActivity::class.java, context)

                            false -> {
                                setContent {
                                    MessengerTheme {
                                        Scaffold(modifier = Modifier.fillMaxSize()) {
                                            it
                                            GreetingInLoginActivity()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        makeToast("Ошибка", context)
                    }
                }
            )
    }


    @Composable
    fun GreetingInLoginActivity() {
        var phoneField by rememberSaveable { mutableStateOf("") }
        var passwordField by rememberSaveable { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainFieldStyle(
                labelText = "Номер телефона",
                enable = true,
                maxLine = 1,
                text = phoneField
            ) { phoneField = it }

            Spacer(modifier = Modifier.height(8.dp))

            MainFieldStyle(
                labelText = "Пароль",
                enable = true,
                maxLine = 1,
                text = passwordField
            ) { passwordField = it }

            Spacer(modifier = Modifier.height(120.dp))

            Button(
                onClick = {
                    val pattern = Regex("[^\\d+]")
                    var formattedPhone = phoneField.replace(pattern, "")
                    formattedPhone = if (!formattedPhone.startsWith("+")) "+$formattedPhone" else formattedPhone

                    val phoneFormat = Regex("(\\+\\d+)(\\d{3})(\\d{3})(\\d{4})")
                    formattedPhone = phoneFormat.replace(formattedPhone) { match ->
                        "${match.groups[1]?.value} ${match.groups[2]?.value}-${match.groups[3]?.value}-${match.groups[4]?.value}"
                    }

                    checkPhone(phoneField, passwordField)
                }
            ) {
                Text("Sign in", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            SignUpInLoginActivity()
        }
    }

    @Composable
    fun SignUpInLoginActivity() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "У вас нет аккаунта?\nДавайте зарегистрируемся!",
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { goTo(SingUpActivity::class.java, context) }
            ) {
                Text("Sign up", fontSize = 18.sp)
            }
        }
    }

    private fun checkPhone(phone: String, password: String) {
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                run breaking@{
                    snapshot.children.forEach { snapshotPhone -> //Перебираем все номера телефонов
                        if (snapshotPhone.key == phone) { //Если номер телефона совпадает с введённым
                            downloadInfoOfUser(
                                snapshotPhone,
                                phone,
                                password
                            ) //Получаем информацию о пользователе
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
                    when (password == USER.password) {
                        true -> {
                            initLoginCallBack(phone, password)  //Обработка запроса
                            authUser(context, phone, callBack) //Аунтетифицируем пользователя
                        }
                        else -> makeToast("Неверный пароль!", context)
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
                    whenSelect (
                        it.isSuccessful,
                        makeToast("Добро пожаловать!", context),
                        makeToast("Error!", context)
                    )
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

