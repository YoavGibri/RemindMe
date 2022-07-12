package com.hirshler.remindme

import android.content.Context
import android.content.SharedPreferences
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

        private const val ALARM_SOUNDS: String = "alarm_sounds"
        fun getAlarmSoundsList(): MutableList<AlarmSound> {
            val listJson = sp.getString(ALARM_SOUNDS, null)
            val soundsList: MutableList<AlarmSound> =
                if (listJson?.isNotEmpty() == true) {
                    Gson().fromJson(listJson, object : TypeToken<MutableList<AlarmSound>>() {}.type)
                } else {
                    AppSettings.initNewAlarmSoundsList()
                }


            return soundsList
        }

        fun setAlarmSoundsList(list: MutableList<AlarmSound>) {
            sp.edit { putString(ALARM_SOUNDS, Gson().toJson(list, object : TypeToken<MutableList<AlarmSound>>() {}.type)) }
        }

    }
}