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
import com.example.messenger.dataBase.firebaseFuns.getMessageKey
import com.example.messenger.dataBase.firebaseFuns.uploadFileToStorage
import com.example.messenger.modals.MessageModal
import com.example.messenger.screens.chatScreens.appVoiceRecorder
import com.example.messenger.ui.theme.textMes
import com.example.messenger.utils.voice.AppVoicePlayer
import com.example.messenger.utils.Constants.TYPE_VOICE
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast

@Composable
fun VoiceMsg(
    pair: Pair<MessageModal, Any>
) {
    val clickOnButton = remember { mutableIntStateOf(0) }
    var appVoicePlayer: AppVoicePlayer = remember { AppVoicePlayer().apply { initMediaPlayer() } }

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
                    clickOnButton.intValue += 1
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
            playVoiceMsg(appVoicePlayer, pair.first) {
                release(clickOnButton, appVoicePlayer)
            }
        }

        2 -> {
            stopVoiceMsg(appVoicePlayer, clickOnButton) {
                release(clickOnButton, appVoicePlayer)
            }
        }
    }
}

private fun playVoiceMsg(
    appVoicePlayer: AppVoicePlayer,
    messageModal: MessageModal,
    function: () -> Unit
) {
    appVoicePlayer.preparePlaying(messageModal.id, messageModal.info) {
        function()
    }
}

private fun stopVoiceMsg(
    appVoicePlayer: AppVoicePlayer,
    clickOnButton: MutableIntState,
    function: () -> Unit
) {
    appVoicePlayer.stopPlay {
        clickOnButton.intValue = 0
        function()
    }
}

fun startRecordVoiceMsg(
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

fun stopRecordVoiceMsg(
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
        }
    } catch (e: Exception) {
        makeToast(e.message.toString() + " ошибка остановки", mainActivityContext)

    } finally {
        changeColor.value = Color.Red
        recordVoiceFlag.value = false
    }
}

private fun release(
    clickOnButton: MutableIntState,
    appVoicePlayer: AppVoicePlayer
) {
    clickOnButton.intValue = 0
    appVoicePlayer.releaseMediaPlayer()
}