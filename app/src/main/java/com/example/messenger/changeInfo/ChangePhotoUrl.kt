package com.example.messenger.changeInfo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messenger.R
import com.example.messenger.utilis.MainImage

@Composable
fun ChangePhotoUrl() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.padding(40.dp))
        MainImage(image = R.drawable.tank, dp = 200.dp) {}
    }
    //TODO доделать страницу изменения фото профиля и доделать профиль
}