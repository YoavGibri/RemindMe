package com.hirshler.remindme

import android.app.Activity
import android.media.MediaRecorder
import android.util.Log
import com.hirshler.remindme.model.Reminder
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import java.io.IOException
import java.util.*

class VoiceRecorderManager(private val activity: Activity, val reminder: Reminder, val onRecordCallback: () -> Unit, val onStopCallback: () -> Unit) {

    private var ringManager: RingManager? = null
//    private var fileName = reminder.voiceNotePath
    private val LOG_TAG = "AudioRecord"

    private var recorder: MediaRecorder? = null


    fun startRecording() {
        Permissions.check(activity, android.Manifest.permission.RECORD_AUDIO, "Please allow permission to record voice reminder",
            object : PermissionHandler() {
                override fun onGranted() {

                    if (reminder.voiceNotePath.isEmpty())
                        reminder.voiceNotePath = "${App.applicationContext().externalCacheDir?.absolutePath}/${Calendar.getInstance().timeInMillis}.3gp"

                    recorder = MediaRecorder().apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        setOutputFile(reminder.voiceNotePath)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                        try {
                            prepare()
                        } catch (e: IOException) {
                            Log.e(LOG_TAG, "prepare() failed")
                        }

                        start()
                        onRecordCallback()
                    }
                }
            })

    }


    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
//        reminder.voiceNotePath = if (this::fileName.isInitialized) fileName else ""
        onStopCallback()
    }

    fun playPreview() {
        ringManager = RingManager(activity).apply {
            setRingPath(reminder.voiceNotePath)
            play(vibrate = false)
        }
    }

    fun stopPreview() {
        ringManager?.pause()
    }


    fun isRecording(): Boolean = recorder != null

    fun onStopRecord() {
        recorder?.release()
        recorder = null
    }
}