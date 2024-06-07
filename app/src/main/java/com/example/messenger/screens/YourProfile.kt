package com.example.messenger.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.messenger.utilsFilies.MainImage
import com.example.messenger.utilsFilies.USER

@Composable
fun YourProfile() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(10.dp)) //Отступ
        MainImage(dp = 200.dp, uri = USER.photoUrl) {}
        Spacer(modifier = Modifier.padding(10.dp)) //Отступ
        HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия
        Spacer(modifier = Modifier.padding(10.dp)) //Отступ
        Column(horizontalAlignment = Alignment.Start) {
            Row {
                extracted("Ваше ФИО: ", USER.fullname)
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия

            Row {
                extracted("Ваш номер телефона: ", USER.phone)
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия

            Row {
                extracted("Ваш никнейм: ", USER.username)
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия

            Row {
                extracted("Цитата: ", USER.bio)
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия


        }


    }
}

@Composable
private fun extracted(string: String, info: String) {
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = string, textAlign = TextAlign.Start)
    Text(text = info, textAlign = TextAlign.Start)
}