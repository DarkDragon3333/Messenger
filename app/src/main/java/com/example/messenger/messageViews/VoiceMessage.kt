package com.example.messenger.messageViews

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
    pair: Pair<MessageModal, Any>
) {
    val clickOnButton = remember { mutableIntStateOf(0) }
    var appVoicePlayer: AppVoicePlayer

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
                    //makeToast("Идёт запись", mainActivityContext)
                    clickOnButton.intValue += 1
                    appVoicePlayer = AppVoicePlayer().apply { initMediaPlayer() }
                    controlVoiceButton(clickOnButton, appVoicePlayer, pair)
                }
            ) {
                ControlIconOfPlayButton(clickOnButton)
            }

            Text(
                text = "Это голосовое сообщение",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 60.dp)
            )

        }

        Text(
            text = pair.second.toString(),
            fontSize = 10.sp,
            modifier = Modifier
                .background(textMes)
                .padding(end = 6.dp, bottom = 2.dp)
                .align(Alignment.BottomEnd)
        )
    }

}

@Composable
private fun ControlIconOfPlayButton(clickOnButton: MutableIntState) {
    when (clickOnButton.intValue) {
        1 ->
            Icon(
                painter = painterResource(id = R.drawable.sharp_autopause_24),
                contentDescription = null
            )

        else ->
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
    }
}


private fun controlVoiceButton(
    clickOnButton: MutableIntState,
    appVoicePlayer: AppVoicePlayer,
    pair: Pair<MessageModal, Any>
) {
    when (clickOnButton.intValue) {
        1 -> {
            play(appVoicePlayer, pair.first) {
                release(clickOnButton, appVoicePlayer)
            }
        }

        2 -> {
            stop(appVoicePlayer, clickOnButton) {
                release(clickOnButton, appVoicePlayer)
            }
        }
    }
}


private fun release(
    clickOnButton: MutableIntState,
    appVoicePlayer: AppVoicePlayer
) {
    clickOnButton.intValue = 0
    appVoicePlayer.releaseMediaPlayer()
}

fun startRecord(
    changeColor: MutableState<Color>,
    receivingUserID: String,
    recordVoiceFlag: MutableState<Boolean>
) {
    try {
        changeColor.value = Color.Blue
        val messageKey = getMessageKey(receivingUserID)
        appVoiceRecorder.startRecording(messageKey)
        recordVoiceFlag.value = true
    } catch (e: Exception) {
        makeToast(e.message.toString() + " ошибка записи", mainActivityContext)
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

fun stopRecord(
    receivingUserID: String,
    changeColor: MutableState<Color>,
    recordVoiceFlag: MutableState<Boolean>
) {
    try {
        appVoiceRecorder.stopRecording { file, messageKey ->
            if (file.exists() && file.length() > 0 && file.isFile && messageKey.isNotEmpty()) {
                val list = listOf(messageKey to Uri.fromFile(file))
                changeColor.value = Color.Red
                recordVoiceFlag.value = false
                uploadFileToStorage(
                    list,
                    receivingUserID,
                    TYPE_VOICE
                )
            } else file.delete()
            changeColor.value = Color.Red
            recordVoiceFlag.value = false
        }
    } catch (e: Exception) {
        makeToast(e.message.toString() + " ошибка остановки", mainActivityContext)
        changeColor.value = Color.Red
        recordVoiceFlag.value = false
    }

}

