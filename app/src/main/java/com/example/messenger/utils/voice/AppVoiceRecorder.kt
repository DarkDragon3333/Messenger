package com.example.messenger.utils.voice

import android.content.Context
import android.media.MediaRecorder
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class AppVoiceRecorder {
    private val mMediaRecorder = MediaRecorder(mainActivityContext)
    private lateinit var mAudioFile: File
    private lateinit var mMessageKey: String

    fun startRecording(messageKey: String, context: Context) = CoroutineScope(Dispatchers.IO).launch {
        try {
            mMessageKey = messageKey
            createAudioFile()
            prepareMediaRecorder(context)
        } catch (e: IOException) {
            makeToast(e.message.toString() + "ошибка в startRecording", context)
        }

    }

    fun stopRecording(onSuccess: (file: File, messageKey: String) -> Unit) {
        try {
            if (mAudioFile.isFile && mAudioFile.exists() && mAudioFile.length() > 0 && mMessageKey.isNotEmpty()) {
                mMediaRecorder.stop()
                onSuccess(mAudioFile, mMessageKey)
            }
        } catch (e: IOException) {
            makeToast(e.message.toString() + "ошибка в stopRec", mainActivityContext)
            if (mAudioFile.exists() && mAudioFile.length() > 0) {
                mAudioFile.delete() //Удаляем ненужный файл
            }

        }
    }

    fun releaseRecorder() {
        try {
            mMediaRecorder.release() //Удаляем экземпляр в телефоне
        } catch (e: IOException) {
            makeToast(e.message.toString() + "ошибка в releaseRecordedVoice", mainActivityContext)
        }
    }

    private fun createAudioFile() {
        mAudioFile = File(
            mainActivityContext.filesDir,
            mMessageKey
        ) //Объявили файл и указали, где он будет храниться
        mAudioFile.createNewFile() //Создали файл
    }

    private suspend fun prepareMediaRecorder(context: Context) {
        try {
            mMediaRecorder.apply {
                reset() //Сбрасываем рекордер, чтобы не было конфликтов с тем, с чем он работал раньше
                setAudioSource(MediaRecorder.AudioSource.DEFAULT) //
                setOutputFormat(MediaRecorder.OutputFormat.DEFAULT) //выходной формат файла
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT) //формат кодирования файла
                setOutputFile(mAudioFile.absolutePath) //Указывыаем путь, куда сохранять аудиозапись
                prepare() //Подготовливаемся к записи аудио
                start() //Запускаем запись аудио
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                makeToast("prepareMediaRecorder " + e.message.toString(), context)
            }
        }
    }
}

