package com.hirshler.remindme.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import com.google.gson.Gson
import com.hirshler.remindme.App
import com.hirshler.remindme.activities.AlertActivity
import com.hirshler.remindme.activities.MainActivity
import com.hirshler.remindme.model.Reminder
import kotlin.math.log

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // TODO: 09/10/21 add receiver to manifest
         Log.d("AlertReceiver", Gson().toJson(intent))
         Log.d("AlertReceiver", context.toString())


        context?.startActivity(
            Intent(context, MainActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
                putExtra("reminder", intent?.getStringExtra("reminder"))
            })
    }
}