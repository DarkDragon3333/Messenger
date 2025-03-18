package com.example.messenger.messageView

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.messenger.R
import com.example.messenger.dataBase.TYPE_VOICE
import com.example.messenger.dataBase.getMessageKey
import com.example.messenger.dataBase.uploadFileToStorage
import com.example.messenger.modals.MessageModal
import com.example.messenger.screens.chatScreens.appVoiceRecorder
import com.example.messenger.ui.theme.textMes
import com.example.messenger.utilsFilies.AppVoicePlayer
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast

@Composable
fun VoiceMsg(
    messageModal: MessageModal,
    timeStamp: String,
    navController: NavHostController
) {
    val clickOnButton = remember { mutableIntStateOf(0) }
    val appVoicePlayer = AppVoicePlayer()
    appVoicePlayer.initMediaPlayer()
    Box(contentAlignment = Alignment.BottomEnd) {
        Row(
            modifier = Modifier
                .background(textMes)
                .padding(8.dp)
        )
        {
            IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
                    .background(Color.White),
                onClick = {
                    makeToast("Идёт запись", mainActivityContext)
                    clickOnButton.intValue += 1

                    when (clickOnButton.intValue) {
                        0 -> {}
                        1 -> {
                            play(appVoicePlayer, messageModal) {
                                clickOnButton.intValue = 0
                            }

                        }

                        2 -> {
                            stop(appVoicePlayer, clickOnButton) {
                                clickOnButton.intValue = 0
                            }
                        }
                    }
                }
            ) {
                when (clickOnButton.intValue) {
                    0 -> Icon(Icons.Default.PlayArrow, contentDescription = "")
                    1 ->
                        Icon(
                            painter = painterResource(id = R.drawable.sharp_autopause_24),
                            contentDescription = ""
                        )

                    2 -> Icon(Icons.Default.PlayArrow, contentDescription = "")
                }
            }

            Text(
                text = "Это голосовое сообщение",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 60.dp)
            )

        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .background(textMes)
        ) {
            Text(
                text = timeStamp,
                fontSize = 10.sp,
                modifier = Modifier
                    .padding(end = 6.dp, bottom = 2.dp)
            )
        }
    }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        if (destination.route != "chatScreen/{fullname}/{status}/{photoURL}/{id}") {
            appVoicePlayer.releaseMediaPlayer()
        }
    }

}

private fun stop(
    appVoicePlayer: AppVoicePlayer,
    clickOnButton: MutableIntState,
    function: () -> Unit
) {
    appVoicePlayer.stopPlaying {
        clickOnButton.intValue = 0
        function()
    }
}

private fun play(
    appVoicePlayer: AppVoicePlayer,
    messageModal: MessageModal,
    function: () -> Unit
) {
    appVoicePlayer.startPlaying(messageModal.id, messageModal.info) {
        function()
    }
}

fun stopRecord(
    receivingUserID: String,
    changeColor: MutableState<Color>,
    recordVoiceFlag: MutableState<Boolean>
) {
    appVoiceRecorder.stopRecording { file, messageKey ->
        val list =
            listOf(messageKey to Uri.fromFile(file))
        uploadFileToStorage(
            list,
            receivingUserID,
            TYPE_VOICE
        )
    }
    changeColor.value = Color.Red
    recordVoiceFlag.value = false
}

fun startRecord(
    changeColor: MutableState<Color>,
    receivingUserID: String,
    recordVoiceFlag: MutableState<Boolean>
) {
    changeColor.value = Color.Blue
    val messageKey =
        getMessageKey(receivingUserID)
    appVoiceRecorder.startRecording(messageKey)
    recordVoiceFlag.value = true
}