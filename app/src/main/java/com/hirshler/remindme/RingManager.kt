package com.hirshler.remindme


import android.content.Context
import android.media.MediaPlayer
import android.net.Uri


class RingManager private constructor(val context: Context, path: String?) {

    private var mp: MediaPlayer = MediaPlayer()

    init {
        tryWithCatch {
            val ringPath = if (path.isNullOrEmpty()) getDefault() else Uri.parse(path) //SP.getDefaultRingtonePath()
            mp.isLooping = true
            mp.setDataSource(context, ringPath)
            mp.prepare()
        }
    }

    companion object {
        private var instance: RingManager? = null

        fun getInstance(context: Context, path: String?): RingManager {
            if (instance == null) {
                instance = RingManager(context, path)
            }
            return instance!!
        }


    }


    fun play() {
        tryWithCatch {
            if (!mp.isPlaying)
                mp.start()
        }
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
//        val alarmTone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val alarmTone: Uri = Uri.parse("android.resource://" + App.applicationContext().packageName + "/" + R.raw.default_alarm)
        return alarmTone;
    }

}