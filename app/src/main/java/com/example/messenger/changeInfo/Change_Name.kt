package com.example.messenger.changeInfo

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
import com.example.messenger.utilis.CHILD_FULLNAME
import com.example.messenger.utilis.USER
import com.example.messenger.utilis.changeInfo
import com.example.messenger.utilis.makeToast

@Composable
fun ChangeName() {
    val context = LocalContext.current
    val fullname = USER.fullname.split(" ")

    var name by remember { mutableStateOf(fullname[0]) }
    var surname by remember { mutableStateOf(fullname[1]) }

    var fullnameFromField = ""
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.padding(0.dp, 40.dp))
        Text(text = "Введите новое имя и фамилию:")

        Spacer(modifier = Modifier.padding(0.dp, 10.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            placeholder = { Text(text = "Введите имя") }
        )

        Spacer(modifier = Modifier.padding(0.dp, 10.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = surname,
            onValueChange = { surname = it },
            placeholder = { Text(text = "Введите фамилию") }
        )

        Spacer(modifier = Modifier.padding(0.dp, 40.dp))
        Button(
            onClick = {
                if (name == "") {
                    makeToast("Введите имя", context)
                } else {
                    changeInfo("$name $surname", CHILD_FULLNAME, context)
                }

            }
        )
        {
            Icon(Icons.Default.Check, contentDescription = "")
            Text(text = "Подтвердить")
        }
    }

}


