package com.hirshler.remindme

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.gson.Gson
import com.hirshler.remindme.model.Alert
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.receivers.AlertReceiver
import java.util.*

class AlertsManager {

    companion object {
        private val toast: Toast? = null

        fun setAlert(reminder: Reminder, alert: Alert, time: Long = 0) {
            val alertTime = if (time != 0L) time else alert.time

            val alarmManager = App.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val now = Calendar.getInstance().timeInMillis


            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alertTime, createPendingIntent(reminder, alert))


            //toast.showInDebug("alarm ${alert.id} set to ${(alertTime - now) / 1000} seconds")
        }

        fun cancelAlert(reminder: Reminder, alert: Alert) {
            val alarmManager = App.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val pendingIntent = createPendingIntent(reminder, alert)

            alarmManager.cancel(pendingIntent);

            toast.showInDebug("alarm ${alert.id} canceled")
        }

        private fun createPendingIntent(reminder: Reminder, alert: Alert): PendingIntent {
            val context = App.applicationContext()
            val intent = Intent(context, AlertReceiver::class.java).apply {
                putExtra("reminder", Gson().toJson(reminder))
            }

            return PendingIntent.getBroadcast(context, alert.id.toInt(), intent, PendingIntent.FLAG_IMMUTABLE)
        }


    }
}

