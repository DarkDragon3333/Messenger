package com.example.messenger.changeNumberPhone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.R
import com.example.messenger.changeNumberPhone.ui.theme.MessengerTheme

class EnterCode : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting2(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    var phone by remember { mutableStateOf("") }
    val context = LocalContext.current
    val maxCount = 6
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(100.dp))
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(bitmap = ImageBitmap.imageResource(R.drawable.sms100), contentDescription ="SMS_image" )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Мы отправили вам СМС с кодом на ваш номер телефона",
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.padding(40.dp))
            TextField(
                value = phone,
                onValueChange =
                    {
                        if (it.length <= maxCount)
                            phone = it
                    },
                supportingText = {
                    Text(
                        text = "${phone.length} / $maxCount",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("") },
                maxLines = 1,

                label = { Text(text = "СМС код", fontSize = 12.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFDFAFE),
                    unfocusedTextColor = Color(0xff888888),
                    focusedContainerColor = Color(0xFFFDFAFE),
                    focusedTextColor = Color(0xff222222),
                )
            )
        }
    }
}
