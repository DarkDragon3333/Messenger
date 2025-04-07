package com.example.messenger.screens.changeInfoScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.example.messenger.dataBase.USER
import com.example.messenger.dataBase.choseChangeInformation
import com.example.messenger.utilsFilies.Constants.CHILD_FULLNAME
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.mainFieldStyle
import com.example.messenger.utilsFilies.makeToast

@Composable
fun ChangeName(navController: NavHostController) {
    val fullname = USER.fullname.split(" ")

    val name = fullname[0]

    var surname = ""

    if (fullname.size == 2) {
        surname = fullname[1]
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.padding(0.dp, 40.dp))
        Text(text = "Введите новое имя и фамилию:")

        Spacer(modifier = Modifier.padding(0.dp, 10.dp))
        val nameField = mainFieldStyle(
            labelText = "Введите имя",
            enable = true,
            maxLine = 1,
            name,
        ) {}

        Spacer(modifier = Modifier.padding(0.dp, 10.dp))
        val surnameField = mainFieldStyle(
            labelText = "Введите фамилию",
            enable = true,
            maxLine = 1,
            surname
        ) {}

        Spacer(modifier = Modifier.padding(0.dp, 40.dp))
        Button(
            onClick = {
                if (nameField == "") {
                    makeToast("Введите имя", mainActivityContext)
                } else {
                    choseChangeInformation(
                        "$nameField $surnameField",
                        CHILD_FULLNAME,
                        mainActivityContext,
                        navController
                    )
                }

            }
        )
        {
            Icon(Icons.Default.Check, contentDescription = "")
            Text(text = "Подтвердить")
        }
    }

}


