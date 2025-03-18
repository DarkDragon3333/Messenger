package com.example.messenger.utilsFilies

import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class AppVoiceRecorder {

    private val mMediaRecorder = MediaRecorder(mainActivityContext)
    private lateinit var mAudioFile: File
    private lateinit var mMessageKey: String

    fun startRecording(messageKey: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            mMessageKey = messageKey
            createAudioFile()
            prepareMediaRecorder()
        } catch (e: IOException) {
            makeToast(e.message.toString(), mainActivityContext)
        }

    }

    fun stopRecording(onSuccess: (file: File, messageKey: String) -> Unit) {
        try {
            mMediaRecorder.stop()
            onSuccess(mAudioFile, mMessageKey)
        } catch (e: IOException) {
            makeToast(e.message.toString(), mainActivityContext)
            mAudioFile.delete() //Удаляем ненужный файл
        }
    }

    fun releaseRecordedVoice() {
        try {
            mMediaRecorder.release() //Удаляем экземпляр в телефоне
        } catch (e: IOException) {
            makeToast(e.message.toString(), mainActivityContext)
        }
    }

    private fun createAudioFile() {
        mAudioFile = File(
            mainActivityContext.filesDir,
            mMessageKey
        ) //Объявили файл и указали, где он будет храниться
        mAudioFile.createNewFile() //Создали файл
    }

    private fun prepareMediaRecorder() {
        mMediaRecorder.apply {
            reset() //Сбрасываем рекордер, чтобы не было конфликтов с тем, с чем он работал раньше
            setAudioSource(MediaRecorder.AudioSource.DEFAULT) //
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT) //выходной формат файла
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT) //формат кодирования файла
            setOutputFile(mAudioFile.absolutePath) //Указывыаем путь, куда сохранять аудиозапись
            prepare() //Начинаем запись аудио
            start() //Запускаем запись аудио
        }

    }
}