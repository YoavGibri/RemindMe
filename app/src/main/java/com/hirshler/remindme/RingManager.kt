package com.hirshler.remindme


import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri


class RingManager(val context: Context, path: String?) {
    private var mp: MediaPlayer = MediaPlayer()

    init {
        tryWithCatch {
            val ringPath = if (path.isNullOrEmpty())  getDefault() else Uri.parse(path) //SP.getDefaultRingtonePath()
            mp.isLooping = true
            mp.setDataSource(context, ringPath)
            mp.prepare()
        }
    }


    fun play() {
        tryWithCatch { mp.start() }
    }

    fun pause() {
        tryWithCatch { mp.pause() }
    }


    private fun tryWithCatch(whatToDo: () -> Unit) {
        try {
            whatToDo.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
            App.showError(context, e.message ?: "whoops... something happened")
        }
    }

    private fun getDefault(): Uri {
        val alarmTone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        return alarmTone;
    }

}