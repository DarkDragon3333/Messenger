package com.example.messenger.screens.changeInfoScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.dataBase.firebaseFuns.choseChangeInformation
import com.example.messenger.utils.Constants.CHILD_BIO
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.MainFieldStyle
import com.example.messenger.utils.makeToast

@Composable
fun ChangeBIO(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        var bio by rememberSaveable { mutableStateOf(USER.bio) }



        Spacer(modifier = Modifier.padding(0.dp, 100.dp, 0.dp, 0.dp))
        Text(text = "Напишите о себе:")
        Spacer(modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))


            MainFieldStyle(
                labelText = "О себе",
                enable = true,
                maxLine = 1,
                text = bio
            ) { newBio ->
                bio = newBio
            }

        Spacer(modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
        Button(
            onClick = {
                when (bio == "") {
                    true -> makeToast("Напишите о себе!", mainActivityContext)
                    false -> choseChangeInformation(
                        bio,
                        CHILD_BIO,
                        mainActivityContext,
                        navController
                    )
                }
            }
        ) {
            Icon(Icons.Default.Check, contentDescription = "")
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "Подтвердить")
        }
    }
}