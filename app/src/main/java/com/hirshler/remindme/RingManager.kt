package com.hirshler.remindme


import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator


class RingManager(private val context: Context) {

    private var mp: MediaPlayer = MediaPlayer()
    private var vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var pattern = longArrayOf(0, 500, 1000)


    fun setRingPath(ringPath: String?) {
        tryWithCatch {
            val attr = AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_ALARM)
                .build()
            mp.setAudioAttributes(attr)
            mp.isLooping = true
            mp.setDataSource(context, Uri.parse(ringPath))
            mp.prepare()
        }
    }


    fun play(vibrate: Boolean = true) {
        tryWithCatch {
            if (!mp.isPlaying)
                mp.start()
        }
        tryWithCatch {
            if (vibrate && AppSettings.getVibrate())
                startVibrator()
        }
    }

    private fun startVibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            vibrator.vibrate(pattern, 0)
        }
    }

    fun pause() {
        tryWithCatch {
            mp.pause()
        }
        tryWithCatch {
            vibrator.cancel()
        }
    }


    private fun tryWithCatch(whatToDo: () -> Unit) {
        try {
            whatToDo.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
//            App.showError(context, e.message ?: "whoops... something happened")
        }
    }


}