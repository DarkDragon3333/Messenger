package com.example.messenger.utils.voice

import android.media.MediaPlayer
import com.example.messenger.dataBase.firebaseFuns.getFile
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import java.io.File
import java.io.IOException

class AppVoicePlayer {
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mAudioFile: File

    fun preparePlaying(messageKey: String, fileUrl: String, function: () -> Unit) {
        mAudioFile = File(mainActivityContext.filesDir, messageKey)
        initMediaPlayer()
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
                    stopPlay{
                        function()
                    }
                }
            }

        } catch (e: IOException) {
            makeToast(e.message.toString(), mainActivityContext)
        }
    }

    fun stopPlay(function: () -> Unit) {
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