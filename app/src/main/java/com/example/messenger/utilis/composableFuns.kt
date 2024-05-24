package com.example.messenger.utilis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.messenger.screens.navButtonBack
import com.example.messenger.user_sing_in_and_up.ui.theme.BlackText
import com.example.messenger.user_sing_in_and_up.ui.theme.BlueButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun mainFieldStyle(
    labelText: String,
    enable: Boolean,
    maxLine: Int,
    action: () -> Unit,
): String {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                action()
            },
        label = { Text(text = labelText, fontSize = 12.sp) },
        maxLines = maxLine,
        enabled = enable,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = BlueButton,
            unfocusedTextColor = BlackText,

            focusedContainerColor = BlueButton,
            focusedTextColor = BlackText,

            disabledContainerColor = BlueButton,
            disabledTextColor = BlackText,
            disabledLabelColor = BlackText,
            disabledIndicatorColor = BlackText,

            )
    )
    return text
}

@Composable
fun mainFieldStyle(
    labelText: String,
    enable: Boolean,
    maxLine: Int,
    mText: String,
    action: () -> Unit,
): String {
    var text by remember { mutableStateOf(mText) }
    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                action()
            },
        label = { Text(text = labelText, fontSize = 12.sp) },
        maxLines = maxLine,
        enabled = enable,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = BlueButton,
            unfocusedTextColor = BlackText,

            focusedContainerColor = BlueButton,
            focusedTextColor = BlackText,

            disabledContainerColor = BlueButton,
            disabledTextColor = BlackText,
            disabledLabelColor = BlackText,
            disabledIndicatorColor = BlackText,
        )
    )
    return text
}

@Composable
fun MainFieldStyle(
    labelText: String,
    enable: Boolean,
    maxLine: Int,
    keyboardOptions:
    KeyboardOptions,
    mText: String,
    function: () -> Unit
) {
    var text by remember { mutableStateOf(mText) }
    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                function()
            },
        label = { Text(text = labelText, fontSize = 12.sp) },
        maxLines = maxLine,
        enabled = enable,
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = BlueButton,
            unfocusedTextColor = BlackText,

            focusedContainerColor = BlueButton,
            focusedTextColor = BlackText,

            disabledContainerColor = BlueButton,
            disabledTextColor = BlackText,
            disabledLabelColor = BlackText,
            disabledIndicatorColor = BlackText,
        )
    )
}

@Composable
fun NavIconButton(
    coroutineScope: CoroutineScope,
    drawerState: DrawerState
) {
    IconButton(
        onClick = {
            coroutineScope.launch {
                drawerState.open()
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu icon"
        )
    }
}

@Composable
fun NavIconButton(
    coroutineScope: CoroutineScope,
    navController: NavHostController
) {
    IconButton(
        onClick = {
            coroutineScope.launch {
                navButtonBack(navController)
            }
        }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back icon"
        )
    }
}