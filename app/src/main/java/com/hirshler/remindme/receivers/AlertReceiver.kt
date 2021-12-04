package com.hirshler.remindme.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hirshler.remindme.Utils
import com.hirshler.remindme.model.Reminder

class AlertReceiver : BroadcastReceiver() {
    private val toast: Toast? = null

    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {
//        val reminderId = intent?.getLongExtra(KEY_REMINDER_ID, -1)
//        toast.showInDebug("alarm ${reminder.alerts?.get(0)?.id} onReceive")

        context?.let {
            intent?.getLongExtra(Reminder.KEY_REMINDER_ID, -1)?.let { id ->
                val alertIntent = Utils.getAlertIntent(id)
                context.startActivity(alertIntent)
            }

        }

    }
}