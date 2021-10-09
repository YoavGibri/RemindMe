package com.hirshler.remindme

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.hirshler.remindme.model.Alert
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.receivers.AlertReceiver
import java.util.*

class AlertsManager() {

    // private lateinit var alarmIntent: PendingIntent

    companion object {


        fun setAlert(reminder: Reminder, alert: Alert) {
            val context = App.applicationContext()
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, AlertReceiver::class.java).apply {
                action = "action yeh yeh"
                putExtra("reminder", Gson().toJson(reminder))
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                getRequestCode(alert),
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
//                alert.time,
                Calendar.getInstance().apply { add(Calendar.SECOND, 5) }.timeInMillis,
                pendingIntent
            )

        }

        private fun getRequestCode(alert: Alert): Int {
            return (alert.time % 100000).toInt()
        }
    }
}

