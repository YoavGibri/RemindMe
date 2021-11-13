package com.hirshler.remindme

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.model.Reminder.Companion.KEY_REMINDER_ID
import com.hirshler.remindme.receivers.AlertReceiver
import java.util.*

class AlertsManager {

    companion object {
        private val toast: Toast? = null

        fun setNextAlert(reminder: Reminder, time: Long = 0) {
            val alertTime = if (time != 0L) time else reminder.nextAlarmTime

            val alarmManager = App.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alertTime, createPendingIntent(reminder))

            val now = Calendar.getInstance().timeInMillis
            toast.showInDebug("alarm ${reminder.id} set to ${(alertTime - now) / 1000} seconds")
        }

        fun cancelAlert(reminder: Reminder) {
            val alarmManager = App.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val pendingIntent = createPendingIntent(reminder)

            alarmManager.cancel(pendingIntent);

            toast.showInDebug("alarm ${reminder.id} canceled")
        }

        private fun createPendingIntent(reminder: Reminder): PendingIntent {
            val context = App.applicationContext()
            val intent = Intent(context, AlertReceiver::class.java).apply {
                putExtra(KEY_REMINDER_ID, reminder.id)
            }

            return PendingIntent.getBroadcast(context, reminder.id?.toInt() ?: -1, intent, PendingIntent.FLAG_IMMUTABLE)
        }


    }
}

