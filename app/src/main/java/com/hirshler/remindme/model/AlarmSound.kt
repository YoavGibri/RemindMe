package com.hirshler.remindme.model

import android.media.RingtoneManager
import android.net.Uri
import com.hirshler.remindme.App

//class AlarmSound<out CharSequence> {
data class AlarmSound(var uri: Uri, var displayName: String = "") {


    init {
        if (displayName.isEmpty())
            displayName = RingtoneManager.getRingtone(App.applicationContext(), uri).getTitle(App.applicationContext())
    }

    override fun toString(): String {
        return displayName
    }
}