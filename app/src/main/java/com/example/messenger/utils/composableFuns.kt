package com.example.messenger.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.messenger.R
import com.example.messenger.screens.navBackButton
import com.example.messenger.viewModals.NavDrawerViewModal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainFieldStyle(
    labelText: String,
    enable: Boolean,
    maxLine: Int,
    text: String,
    action: (String) -> Unit,
) {

    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.Gray,
        backgroundColor = Color.Gray
    )
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            value = text,
            onValueChange = { action(it) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    action(text)
                },
            label = {
                Text(
                    text = labelText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            maxLines = maxLine,
            enabled = enable,
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.outline,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                cursorColor = Color.Gray
            )
        )
    }
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
        label = { Text(
            text = labelText,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ) },
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        maxLines = maxLine,
        enabled = enable,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
            //cursorColor = MaterialTheme.colorScheme.primary
        )
    )
    return text
}

@Composable
fun MainFieldStyle(
    labelText: String,
    enable: Boolean,
    maxLine: Int,
    keyboardOptions: KeyboardOptions,
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
        label = { Text(
            text = labelText,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ) },
        maxLines = maxLine,
        enabled = enable,
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
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
    navController: NavHostController,
    navDrawerViewModal: NavDrawerViewModal
) {
    IconButton(
        onClick = {
            coroutineScope.launch {
                navBackButton(navController, navDrawerViewModal)
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
fun UriImage(
    dp: Dp,
    uri: String,
    action: () -> Unit,
) {
    val context = LocalContext.current
    val imageRequest = remember(uri) {
        ImageRequest.Builder(context)
            .data(uri)
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = "",
        placeholder = painterResource(R.drawable.def_image_msg),
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
fun MessageImage(uri: String) {
    val context = LocalContext.current
    val imageRequest = remember(uri) {
        ImageRequest.Builder(context)
            .data(uri)
            .size(200)
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = null,
        placeholder = painterResource(R.drawable.def_image_msg),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}