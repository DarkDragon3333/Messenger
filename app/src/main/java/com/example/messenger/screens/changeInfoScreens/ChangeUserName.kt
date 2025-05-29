package com.example.messenger.screens.changeInfoScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.dataBase.firebaseFuns.choseChangeInformation
import com.example.messenger.utilsFilies.Constants.CHILD_USER_NAME
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.mainFieldStyle
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.whenSelect

@Composable
fun ChangeUserName(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(modifier = Modifier.padding(0.dp, 100.dp, 0.dp, 0.dp))
        Text(
            text = "Введите ваш новый никнейм. Пишите всё с маленькой буквы!",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
        val changeUserNameField = mainFieldStyle(
            labelText = "Никнейм",
            enable = true,
            maxLine = 1,
            USER.username
        ) {}
        Spacer(modifier = Modifier.padding(0.dp, 40.dp, 0.dp, 0.dp))
        Button(
            onClick = {
                whenSelect(
                    bool = changeUserNameField == "",
                    funTrue = makeToast("Введите никнейм!", mainActivityContext),
                    funFalse = choseChangeInformation(
                        changeUserNameField,
                        CHILD_USER_NAME,
                        mainActivityContext,
                        navController
                    )
                )
            }
        ) {
            Icon(Icons.Default.Check, contentDescription = "")
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "Подтвердить")
        }
    }
}

