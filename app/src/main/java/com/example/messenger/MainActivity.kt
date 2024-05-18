package com.example.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.messenger.screens.NavDrawer
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.utilis.AUTH
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MessengerTheme {
                NavDrawer()
            }
        }
        init()
    }

    private fun init(){
        AUTH = FirebaseAuth.getInstance()
    }
}
//Todo. Доделать дизайн.
// Сделать макет страниц профиля, настроек, о программе, инструкцию







