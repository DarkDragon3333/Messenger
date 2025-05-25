package com.example.messenger.screens.changeInfoScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.dataBase.firebaseFuns.choseChangeInformation
import com.example.messenger.utilsFilies.Constants.CHILD_BIO
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.mainFieldStyle
import com.example.messenger.utilsFilies.makeToast

@Composable
fun ChangeBIO(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(modifier = Modifier.padding(0.dp, 100.dp, 0.dp, 0.dp))
        Text(text = "Напишите о себе:")
        Spacer(modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
        val bio = mainFieldStyle(
            labelText = "О себе",
            enable = true,
            maxLine = 1,
            USER.bio
        ) {}
        Spacer(modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
        Button(
            onClick = {
                if (bio == "") {
                    makeToast("Напишите о себе!", mainActivityContext)
                } else {
                    choseChangeInformation(bio, CHILD_BIO, mainActivityContext, navController)

                }
            }
        ) {
            Icon(Icons.Default.Check, contentDescription = "")
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "Подтвердить")
        }
    }

}