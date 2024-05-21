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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.MainActivity
import com.example.messenger.user_sing_in_and_up.ui.theme.MessengerTheme
import com.example.messenger.utilis.AUTH
import com.example.messenger.utilis.goTo
import com.example.messenger.utilis.initFirebase
import com.example.messenger.utilis.initUser
import com.example.messenger.utilis.mainFieldStyle

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
        initUser(context)//Инициализируем пользователя
        if (AUTH.currentUser != null) { //Если пользователь уже есть
            goTo(MainActivity::class.java, context)
        }
    }

    @Composable
    fun GreetingInLoginActivity(modifier: Modifier = Modifier) {
        val phone by remember { mutableStateOf("") }
        val password by remember { mutableStateOf("") }

        Column {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 100.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                mainFieldStyle(
                    labelText = "Email",
                    enable = true,
                    maxLine = 1
                ) {}
                Spacer(modifier = Modifier.padding(8.dp))
                mainFieldStyle(
                    labelText = "Password",
                    enable = true,
                    maxLine = 1
                ) {}
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

}

