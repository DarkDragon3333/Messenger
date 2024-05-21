package com.example.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.messenger.screens.NavDrawer
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.utilis.AUTH
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    lateinit var context: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContent {
            MessengerTheme {
                NavDrawer()
            }
        }
    }

    private fun init(){
        context = this
        AUTH = FirebaseAuth.getInstance()
    }
}







