package com.hirshler.remindme.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PermissionChangedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // TODO: 09/10/21 add receiver to manifest
        // TODO: 09/10/21 re-set all alarms if canScheduleExactAlarms()
        //https://developer.android.com/training/scheduling/alarms#exact-permission-check
    }
}