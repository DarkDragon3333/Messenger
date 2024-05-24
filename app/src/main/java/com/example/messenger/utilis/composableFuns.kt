package com.example.messenger.utilis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun mainFieldStyle (
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
            unfocusedContainerColor = Color(0xFFF6F8FE),
            unfocusedTextColor = Color(0xff888888),

            focusedContainerColor = Color(0xFFF6F8FE),
            focusedTextColor = Color(0xff222222),

            disabledContainerColor = Color(0xFFF6F8FE),
            disabledTextColor = Color(0xff222222),
            disabledLabelColor = Color(0xff222222),
            disabledIndicatorColor = Color(0xff222222),

        )
    )
    return text
}
@Composable
fun mainFieldStyle (
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
                unfocusedContainerColor = Color(0xFFF6F8FE),
                unfocusedTextColor = Color(0xff222222),

                focusedContainerColor = Color(0xFFF6F8FE),
                focusedTextColor = Color(0xff222222),

                disabledContainerColor = Color(0xFFF6F8FE),
                disabledTextColor = Color(0xff222222),
                disabledLabelColor = Color(0xff222222),
                disabledIndicatorColor = Color(0xff222222),
            )
        )
    return text
}

@Composable
fun mainFieldStyle (
    text: String,
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
            unfocusedContainerColor = Color(0xFFF6F8FE),
            unfocusedTextColor = Color(0xff222222),

            focusedContainerColor = Color(0xFFF6F8FE),
            focusedTextColor = Color(0xff222222),

            disabledContainerColor = Color(0xFFF6F8FE),
            disabledTextColor = Color(0xff222222),
            disabledLabelColor = Color(0xff222222),
            disabledIndicatorColor = Color(0xff222222),
        )
    )
    return text
}