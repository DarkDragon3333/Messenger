package com.example.messenger.changeNumberPhone

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.changeNumberPhone.ui.theme.MessengerTheme

class ChangeNumber : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var phone by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(0.dp, 100.dp, 0.dp, 0.dp).imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Column(modifier = Modifier.fillMaxHeight(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally)
        {

            Text(
                text = "Введите новый номер телефона.\nПотом нажмите на кнопку снизу справа экрана, чтобы\nпродолжить",
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.padding(40.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                label = { Text(text = "Новый номер телефона", fontSize = 12.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFDFAFE),
                    unfocusedTextColor = Color(0xff888888),
                    focusedContainerColor = Color(0xFFFDFAFE),
                    focusedTextColor = Color(0xff222222),
                )
            )
        }
        Row(modifier = Modifier.fillMaxWidth().imePadding() ){
            Row(horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            {
                Spacer(modifier = Modifier.padding(4.dp))
                Button(onClick = { /*TODO*/ }, ) {
                    Text(text = "Назад")
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            )
            {
                Button(
                    onClick = { context.startActivity(Intent(context, EnterCode::class.java)) }
                ) { Text(text = "Продолжить") }
                Spacer(modifier = Modifier.padding(4.dp))
            }


        }

    }

}

