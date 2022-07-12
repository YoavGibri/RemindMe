package com.hirshler.remindme

import android.content.Context
import android.media.AudioManager
import com.google.gson.Gson
import com.hirshler.remindme.model.AlarmSound

class AppSettings {


    companion object {


        private const val KEY_USER_NAME: String = "username"
        private const val IS_DEBUG_MODE: String = "is_debug_mode"
        private const val IS_FIRST_LAUNCH: String = "is_first_launch"
        fun getIsDebugMode(): Boolean {
            return BuildConfig.DEBUG && SP.get().getBoolean(IS_DEBUG_MODE, false)
        }

        fun setIsDebugMode(isDebug: Boolean) {
            SP.set().putBoolean(IS_DEBUG_MODE, isDebug).apply()
        }


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
        fun getGeneralAlarm(): AlarmSound {
            val generalAlarm = SP.get().getString(SETTING_GENERAL_ALARM_SOUND, null)

            return generalAlarm?.let { Gson().fromJson(generalAlarm, AlarmSound::class.java) }
                ?: defaultAlarm()
        }


        fun setGeneralAlarm(sound: AlarmSound) {
            SP.set().putString(SETTING_GENERAL_ALARM_SOUND, Gson().toJson(sound)).apply()
        }

        private fun defaultAlarm() = AlarmSound("android.resource://" + App.applicationContext().packageName + "/" + R.raw.default_alarm, "Default Alarm")
        private fun noAlarm() = AlarmSound("", "Silent")

        fun addSoundToAlarmSounds(newAlarmSound: AlarmSound) {
            val alarmsSounds = SP.getAlarmSoundsList()

            if (alarmsSounds.firstOrNull { alarm -> alarm.stringUri == newAlarmSound.stringUri } == null) {

                alarmsSounds.add(newAlarmSound)
                if (alarmsSounds.size > 8)
                    alarmsSounds.removeAt(2)

                SP.setAlarmSoundsList(alarmsSounds)
            }
        }

        fun initNewAlarmSoundsList(): MutableList<AlarmSound> {
            return mutableListOf(defaultAlarm(), noAlarm())
        }

        fun setUserName(userName: String) {
            SP.set().putString(KEY_USER_NAME, userName).apply()
        }

        fun getUserName(): String {
            return SP.get().getString(KEY_USER_NAME, "")!!
        }

        fun firstLaunch(): Boolean {
            val firstLaunch = SP.get().getBoolean(IS_FIRST_LAUNCH, true)
            if (firstLaunch) {
                SP.set().putBoolean(IS_FIRST_LAUNCH, false).apply()
            }
            return firstLaunch
        }


    }

}