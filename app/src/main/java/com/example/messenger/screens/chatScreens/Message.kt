package com.example.messenger.screens.chatScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Message() {
    Row(modifier = Modifier
        .fillMaxSize()
        .background(Color.Cyan)) {
        Row (horizontalArrangement = Arrangement.Start) {
            Row(modifier = Modifier
                .fillMaxWidth(0.48f)
                .background(Color.Red)
                .border(
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(4.dp)
                ),
                horizontalArrangement = Arrangement.Start) {
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Hi")
                    Text(text = "21:21", fontSize = 10.sp)
                }

            }
        }

        Spacer(modifier = Modifier.width(15.dp))

        Row(horizontalArrangement = Arrangement.End) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(Color.Green)
                .border(
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(4.dp)
                ),
                horizontalArrangement = Arrangement.End) {
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Hi")
                    Text(text = "21:21", fontSize = 10.sp)
                }

            }
        }
    }

}