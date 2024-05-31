package com.example.messenger.screens.changeInfoScreens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.MainActivity
import com.example.messenger.R
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.user_sing_in_and_up_activities.EnterCode
import com.example.messenger.utilsFilies.MainFieldStyle
import com.example.messenger.utilsFilies.USER
import com.example.messenger.utilsFilies.goTo

class ChangeNumber : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    GreetingChangeNumber(Modifier.padding(it))
                }
            }
        }
    }

    @Composable
    fun GreetingChangeNumber(m: Modifier = Modifier) {
        val context = LocalContext.current

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 100.dp, 0.dp, 0.dp)
            .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Column(modifier = Modifier.fillMaxHeight(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text(
                    text = stringResource(R.string.change_number_text),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.padding(40.dp))
                MainFieldStyle(
                    labelText = "Новый номер телефона",
                    enable = true,
                    maxLine = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    mText = USER.phone
                ) {}
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .imePadding() ){
                Row(horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                {
                    Spacer(modifier = Modifier.padding(4.dp))
                    Button(onClick = {
                        goTo(MainActivity::class.java, context)
                    }) { Text(text = "Назад") }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Button(
                        onClick = {
                            goTo(EnterCode::class.java, context)
                        }
                    ) { Text(text = "Продолжить") }
                    Spacer(modifier = Modifier.padding(4.dp))
                }

            }

        }
    }


}




