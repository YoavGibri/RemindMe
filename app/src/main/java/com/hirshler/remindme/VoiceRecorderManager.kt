package com.hirshler.remindme

import android.app.Activity
import android.media.MediaRecorder
import android.util.Log
import com.hirshler.remindme.model.Reminder
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import java.io.IOException
import java.util.*

class VoiceRecorderManager(val activity: Activity, val reminder: Reminder, val onRecordCallback: () -> Unit, val onStopCallback: () -> Unit) {
    private lateinit var fileName: String
    private val LOG_TAG = "AudioRecord"

    private var recorder: MediaRecorder? = null

    fun startRecording() {
        Permissions.check(activity, android.Manifest.permission.RECORD_AUDIO, "Please allow permission to record voice reminder",
            object : PermissionHandler() {
                override fun onGranted() {
                    fileName = "${App.applicationContext().externalCacheDir?.absolutePath}/${Calendar.getInstance().timeInMillis}.3gp"

                    recorder = MediaRecorder().apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        setOutputFile(fileName)
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
        reminder.voiceNotePath = if (this::fileName.isInitialized) fileName else null
        onStopCallback()
    }

    fun isRecording(): Boolean = recorder != null

    fun onStop() {
        recorder?.release()
        recorder = null
    }
}