package com.jarvis.ai.audio

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class AudioPlaybackService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val TAG = "AudioPlaybackService"

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val base64Audio = intent?.getStringExtra("audio_base64")
        if (!base64Audio.isNullOrEmpty()) {
            playAudio(base64Audio)
        }
        return START_NOT_STICKY
    }

    private fun playAudio(base64Audio: String) {
        try {
            mediaPlayer?.release()
            val audioBytes = android.util.Base64.decode(base64Audio, android.util.Base64.DEFAULT)
            val tempFile = java.io.File.createTempFile("jarvis_tts", ".mp3", cacheDir)
            tempFile.writeBytes(audioBytes)
            tempFile.deleteOnExit()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Playback failed", e)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}
