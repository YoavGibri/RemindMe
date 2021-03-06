package com.hirshler.remindme.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hirshler.remindme.App
import com.hirshler.remindme.FlowLog
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.model.Reminder.Companion.KEY_REMINDER_ID
import com.hirshler.remindme.receivers.AlertReceiver
import com.hirshler.remindme.showInDebug
import java.util.*

class AlertsManager {

    companion object {
        private val toast: Toast? = null
        val TAG: String = "AlertsManager"

        fun setNextAlert(reminder: Reminder) {
//            val alertTime = if (time != 0L) time else reminder.nextAlarmWithSnooze()
            val alertTime = reminder.nextAlarmWithSnooze()

            val alarmManager = App.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alertTime, createPendingIntent(reminder))
            val pendingIntent = createPendingIntent(reminder)
            val info = AlarmManager.AlarmClockInfo(alertTime, pendingIntent)
            alarmManager.setAlarmClock(info, pendingIntent)
            FlowLog.setAlert(reminder, alertTime)

            val now = Calendar.getInstance().timeInMillis
            toast.showInDebug("alarm ${reminder.id} set to ${(alertTime - now) / 1000} seconds from now")
        }

        fun cancelAlert(reminder: Reminder) {
            val alarmManager = App.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val pendingIntent = createPendingIntent(reminder)

            alarmManager.cancel(pendingIntent)

            FlowLog.alertCancel(reminder)
            toast.showInDebug("alarm ${reminder.id} canceled")
        }

        private fun createPendingIntent(reminder: Reminder): PendingIntent {
            val context = App.applicationContext()
            val intent = Intent(context, AlertReceiver::class.java).apply {
                putExtra(KEY_REMINDER_ID, reminder.id)
                action = reminder.id.toString()
            }

            return PendingIntent.getBroadcast(context, reminder.id?.toInt() ?: -1, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }


    }
}

