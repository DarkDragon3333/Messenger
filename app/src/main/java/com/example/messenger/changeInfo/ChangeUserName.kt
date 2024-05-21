package com.example.messenger.changeInfo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.messenger.utilis.CHILD_PHONE
import com.example.messenger.utilis.changeInfo
import com.example.messenger.utilis.mainFieldStyle
import com.example.messenger.utilis.makeToast

@Composable
fun ChangeUserName(){
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(0.dp, 100.dp, 0.dp, 0.dp))
        Text(text = "Введите ваш новый никнейм:")
        Spacer(modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
        val phoneField = mainFieldStyle(
            labelText = "Номер телефона",
            enable = true,
            maxLine = 1
        ) {}
        Spacer(modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
        Button(
            onClick = {
                if (phoneField == ""){
                    makeToast("Введите никнейм!", context)
                }
                else {
                    changeInfo(phoneField, CHILD_PHONE, context)
                }
            }
        ) {

        }
    }

}

