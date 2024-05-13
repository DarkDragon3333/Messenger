package com.example.messenger.user_sing_in_and_up

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.user_sing_in_and_up.ui.theme.MessengerTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GreetingInLoginActivity(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)

                    )

                }
            }
        }
    }
}

@Composable
fun GreetingInLoginActivity(name: String, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
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
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Email") }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Password") }
            )
            Spacer(modifier = Modifier.padding(120.dp))
            Button(
                onClick = {

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

                }
            ) {
                Text("Sing up", fontSize = 18.sp)
            }
        }


    }
}