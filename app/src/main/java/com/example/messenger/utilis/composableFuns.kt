package com.example.messenger.utilis

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.messenger.R
import com.example.messenger.screens.navButtonBack
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
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.tertiary,

            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onTertiary,

            disabledContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledTextColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledLabelColor = MaterialTheme.colorScheme.outline,
            disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant,

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
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.tertiary,

            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onTertiary,

            disabledContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledTextColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledLabelColor = MaterialTheme.colorScheme.outline,
            disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
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
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.tertiary,

            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onTertiary,

            disabledContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledTextColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledLabelColor = MaterialTheme.colorScheme.outline,
            disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
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

@Composable
fun MainImage(
    dp: Dp,
    action: () -> Unit,
) {
    AsyncImage(
        model = USER.photoUrl,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(CircleShape)
            .size(dp)
            .clickable {
                action()
            }
    )


}

@Composable
fun MainImage(
    dp: Dp,
    i: Int,
    action: () -> Unit,
) {
    Image(
        bitmap = ImageBitmap.imageResource(R.drawable.tank),
        contentDescription = " ",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(CircleShape)
            .size(dp)
            .clickable {
                action()
            }
    )


}