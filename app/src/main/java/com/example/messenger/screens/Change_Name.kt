package com.example.messenger.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.messenger.MainActivity
import com.example.messenger.utilis.CHILD_FULLNAME
import com.example.messenger.utilis.NODE_USERS
import com.example.messenger.utilis.REF_DATABASE_ROOT
import com.example.messenger.utilis.UID
import com.example.messenger.utilis.USER
import com.example.messenger.utilis.goTo
import com.example.messenger.utilis.makeToast

@Composable
fun ChangeName() {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.padding(0.dp, 40.dp))
        Text(text = "Введите новое имя и фамилию:")

        Spacer(modifier = Modifier.padding(0.dp, 10.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            placeholder = { Text(text = "Имя:") }
        )

        Spacer(modifier = Modifier.padding(0.dp, 10.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = surname,
            onValueChange = { surname = it },
            placeholder = { Text(text = "Фамилия:") }
        )

        Spacer(modifier = Modifier.padding(0.dp, 40.dp))
        Button(
            onClick = {
                if (name == "") {
                    makeToast("Введите имя", context)
                } else {
                    if (surname == "")
                        changeName(name, "", context)
                    else
                        changeName(name, surname, context)
                }

            }
        )
        {
            Icon(Icons.Default.Check, contentDescription = "")
            Text(text = "Подтвердить")
        }
    }

}

fun changeName(name: String, surname: String, context: Context) {
    val fullname = "$name $surname"
    REF_DATABASE_ROOT
        .child(NODE_USERS)
        .child(UID)
        .child(CHILD_FULLNAME)
        .setValue(fullname).addOnCompleteListener {
            if (it.isSuccessful) {
                makeToast("Данные обновлены!", context)
                USER.fullname = fullname
                goTo(MainActivity::class.java, context)
            }
        }

}