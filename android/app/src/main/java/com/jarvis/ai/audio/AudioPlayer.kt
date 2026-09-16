package com.jarvis.ai.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import java.io.File

object AudioPlayer {
    private const val TAG = "AudioPlayer"
    private var mediaPlayer: MediaPlayer? = null

    fun init(context: Context) {
        Log.d(TAG, "AudioPlayer initialized")
    }

    fun playBase64Audio(base64Audio: String?) {
        if (base64Audio.isNullOrEmpty()) return

        try {
            mediaPlayer?.release()
            val audioBytes = android.util.Base64.decode(base64Audio, android.util.Base64.DEFAULT)
            val tempFile = File.createTempFile("jarvis_tts", ".mp3")
            tempFile.writeBytes(audioBytes)
            tempFile.deleteOnExit()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play audio", e)
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
