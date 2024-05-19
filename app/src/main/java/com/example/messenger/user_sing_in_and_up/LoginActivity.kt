package com.example.messenger.user_sing_in_and_up

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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.MainActivity
import com.example.messenger.user_sing_in_and_up.ui.theme.MessengerTheme
import com.example.messenger.utilis.AUTH
import com.example.messenger.utilis.goTo
import com.example.messenger.utilis.initFirebase
import com.example.messenger.utilis.makeToast
import com.google.firebase.Firebase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class LoginActivity : ComponentActivity() {

    private lateinit var context: LoginActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GreetingInLoginActivity(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun init() {
        context = this
        initFirebase() //Инициализируем БД
        if (AUTH.currentUser != null) { //Если пользователь уже есть
            goTo(MainActivity::class.java, context)
        }
    }

    @Composable
    fun GreetingInLoginActivity(modifier: Modifier = Modifier) {
        var phone by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

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
                    onValueChange =
                    {
                        phone = it
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("") },
                    maxLines = 1,
                    label = { Text(text = "Email", fontSize = 14.sp) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFDFAFE),
                        unfocusedTextColor = Color(0xff888888),
                        focusedContainerColor = Color(0xFFFDFAFE),
                        focusedTextColor = Color(0xff222222),
                    )
                )
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("") },
                    maxLines = 1,
                    label = { Text(text = "Password", fontSize = 14.sp) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFDFAFE),
                        unfocusedTextColor = Color(0xff888888),
                        focusedContainerColor = Color(0xFFFDFAFE),
                        focusedTextColor = Color(0xff222222),
                    )
                )
                Spacer(modifier = Modifier.padding(120.dp))

                Button(
                    onClick = {
                        goTo(MainActivity::class.java, context)
                    }
                ) {
                    Text("Sing in", fontSize = 18.sp)
                }

                Box(
                    contentAlignment = Alignment.BottomCenter, modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp, 0.dp, 0.dp, 40.dp)
                ) {
                    SingUpInLoginActivity()
                }

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
                text = "Don't have an account?\nLet's go to sing up!",
                modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp, 60.dp, 0.dp)
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

    private fun checkEnterData(
        phone: String,
        password: String,
    ): Boolean {
        if (phone.isEmpty() or password.isEmpty()) {
            makeToast("Please, enter all data", this)
        } else if (!phone.contains("@")) {
            makeToast("Please, check email", this)
        } else {
            return true
        }
        return false
    }

    private fun workWithBDInLoginActivity(
        phone: String,
        password: String,
    ) {
        val db = Firebase.firestore

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                checkEnterDataWithBD(phone, password, result)
            }
            .addOnFailureListener {
                makeToast("Error", context)
            }
    }

    private fun checkEnterDataWithBD(
        phone: String,
        password: String,
        result: QuerySnapshot,
        //saveSingIn: SharedPreferences
    ) {
        var flag = 0
        for (document in result) {
            if ((phone == document.getString("phone")) and
                (password == document.getString("password"))
            ) {
                flag = -1
            } else if ((phone != document.getString("phone")) or
                (password != document.getString("password"))
            ) {
                flag += 1
            }

            if (flag == result.size() - 1) {
                makeToast("Error of sing in. Check Enter data", context)
            } else if (flag == -1) {
                //saveSingIn.edit().putString("phone", phone.text.toString()).apply()
                //saveSingIn.edit().putString("password", password.text.toString()).apply()
                goTo(MainActivity::class.java, context)
            }
        }
    }

}

