package com.hirshler.remindme

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hirshler.remindme.model.AlarmSound

class SP {
    companion object {

        private val sp: SharedPreferences =
            App.applicationContext().getSharedPreferences("remindMe", Context.MODE_PRIVATE)


        fun set(): SharedPreferences.Editor = sp.edit()

        fun get() = sp

        private const val DEFAULT_RING_PATH: String = "default_ring_path"
        fun getDefaultRingtonePath(): String {
            return sp.getString(DEFAULT_RING_PATH, "")!!
        }

        fun setDefaultRingtonePath(path: String) {
            sp.edit { putString(DEFAULT_RING_PATH, path) }
        }


        private const val IS_DEBUG_MODE: String = "is_debug_mode"
        fun getIsDebugMode(): Boolean {
            return sp.getBoolean(IS_DEBUG_MODE, false)
        }

        fun setIsDebugMode(isDebug: Boolean) {
            sp.edit { putBoolean(IS_DEBUG_MODE, isDebug) }
        }

        private const val ALARM_SOUNDS: String = "alarm_sounds"
        fun getAlarmSoundsList(): MutableList<AlarmSound> {
            val listJson = sp.getString(ALARM_SOUNDS, null)
            val soundsList: MutableList<AlarmSound> =
                if (listJson?.isNotEmpty() == true) {
                    Gson().fromJson(listJson, object : TypeToken<MutableList<AlarmSound>>() {}.type)
                } else {
                    mutableListOf()
                }

            if (soundsList.isEmpty()) {
                soundsList.add(
                    AlarmSound(
                        Uri.parse("android.resource://" + App.applicationContext().packageName + "/" + R.raw.default_alarm),
                        "Default Alarm"
                    )
                )
            }
            return soundsList
        }

        fun setAlarmSoundsList(list: MutableList<AlarmSound>) {
            sp.edit { putString(ALARM_SOUNDS, Gson().toJson(list, object : TypeToken<MutableList<AlarmSound>>() {}.type)) }
        }

    }
}