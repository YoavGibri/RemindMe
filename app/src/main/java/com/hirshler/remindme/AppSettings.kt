package com.hirshler.remindme

import android.content.Context
import android.media.AudioManager
import android.net.Uri

class AppSettings {


    companion object {
        private val initialVolume = (App.applicationContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager)
            .getStreamMaxVolume(AudioManager.STREAM_ALARM) / 2

        private const val SETTING_VOICE_VOLUME: String = "voice_volume"
        fun setVoiceVolume(value: Int) {
            SP.set().putInt(SETTING_VOICE_VOLUME, value).apply()
        }

        fun getVoiceVolume(): Int {
            return SP.get().getInt(SETTING_VOICE_VOLUME, initialVolume)
        }


        private const val SETTING_ALARM_VOLUME: String = "alarm_volume"
        fun setAlarmVolume(value: Int) {
            SP.set().putInt(SETTING_ALARM_VOLUME, value).apply()
        }

        fun getAlarmVolume(): Int {
            return SP.get().getInt(SETTING_ALARM_VOLUME, initialVolume)
        }


        private const val SETTING_VIBRATE: String = "vibrate"
        fun setVibrate(value: Boolean) {
            SP.set().putBoolean(SETTING_VIBRATE, value).apply()
        }

        fun getVibrate(): Boolean {
            return SP.get().getBoolean(SETTING_VIBRATE, true)
        }

        private const val SETTING_CLOSE_APP_AFTER_REMINDER_SET: String = "close_app_after_reminder_set"
        fun setCloseAppAfterReminderSet(value: Boolean) {
            SP.set().putBoolean(SETTING_CLOSE_APP_AFTER_REMINDER_SET, value).apply()
        }

        fun getCloseAppAfterReminderSet(): Boolean {
            return SP.get().getBoolean(SETTING_CLOSE_APP_AFTER_REMINDER_SET, true)
        }


        private const val SETTING_GENERAL_ALARM_SOUND: String = "general_alarm"
        fun getGeneralAlarm(): Uri? {
            val generalAlarm = SP.get().getString(SETTING_GENERAL_ALARM_SOUND, null);
            return generalAlarm?.let { Uri.parse(it) }
        }

        fun setGeneralAlarm(alarmUri: Uri?) {
            val alarm = alarmUri?.toString()
            SP.set().putString(SETTING_GENERAL_ALARM_SOUND, alarm).apply()
        }


    }

}