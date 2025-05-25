package com.example.messenger.utilsFilies.voiceFiles

import android.media.MediaPlayer
import com.example.messenger.dataBase.firebaseFuns.getFile
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import java.io.File
import java.io.IOException

class AppVoicePlayer {
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mAudioFile: File

    fun startPlaying(messageKey: String, fileUrl: String, function: () -> Unit) {
        mAudioFile = File(mainActivityContext.filesDir, messageKey)
        if (mAudioFile.exists() && mAudioFile.length() > 0 && mAudioFile.isFile) {
            play{
                function()
            }
        } else {
            mAudioFile.createNewFile()
            getFile(mAudioFile, fileUrl) {
                play {
                    function()
                }
            }
        }
    }

    private fun play(function: () -> Unit) {
        try {
            mMediaPlayer.apply {
                setDataSource(mAudioFile.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    stopPlaying{
                        function()
                    }
                }
            }

        } catch (e: IOException) {
            makeToast(e.message.toString(), mainActivityContext)
        }
    }

    fun stopPlaying(function: () -> Unit) {
        try {
            mMediaPlayer.apply {
                stop()
                reset()
                function()
            }
        } catch (e: IOException){
            makeToast(e.message.toString(), mainActivityContext)
            function()
        }
    }

    fun releaseMediaPlayer() {
        mMediaPlayer.release()
    }

    fun initMediaPlayer() {
        mMediaPlayer = MediaPlayer()
    }
}