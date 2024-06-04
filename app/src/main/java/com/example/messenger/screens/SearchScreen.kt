package com.example.messenger.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.utilsFilies.mainFieldStyle

@Composable
fun SearchScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(0.dp, 50.dp, 0.dp, 0.dp))
        mainFieldStyle(labelText = "Поиск", enable = true, maxLine = 1, mText = "") {

        }
    }
}