package com.hirshler.remindme.model

import android.media.RingtoneManager
import android.net.Uri
import com.hirshler.remindme.App

//data class AlarmSound(var uri: Uri, var displayName: String = "") {
data class AlarmSound(var stringUri: String, var displayName: String = "") {


    init {
        if (displayName.isEmpty())
            displayName = RingtoneManager.getRingtone(App.applicationContext(), Uri.parse(stringUri)).getTitle(App.applicationContext())
    }

    override fun toString(): String {
        return displayName
    }
}