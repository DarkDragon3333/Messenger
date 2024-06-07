package com.example.messenger.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
                Text(text = "Ваше ФИО: ", textAlign = TextAlign.Start)
                Text(text = USER.fullname, textAlign = TextAlign.Start)
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия

            Row {
                Text(text = "Ваш номер телефона: ", textAlign = TextAlign.Start)
                Text(text = USER.phone, textAlign = TextAlign.Start)
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия

            Row {
                Text(text = "Ваш никнейм: ", textAlign = TextAlign.Start)
                Text(text = USER.username, textAlign = TextAlign.Start)
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия

            Row {
                Text(text = "Цитата: ", textAlign = TextAlign.Start)
                Text(text = USER.bio, textAlign = TextAlign.Start)
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия


        }


    }
}