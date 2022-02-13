package com.hirshler.remindme.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hirshler.remindme.FlowLog
import com.hirshler.remindme.Utils
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.AppDatabase
import kotlinx.coroutines.runBlocking
import java.util.*

class AlertReceiver : BroadcastReceiver() {
    private val toast: Toast? = null


    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {
//        val reminderId = intent?.getLongExtra(KEY_REMINDER_ID, -1)
//        toast.showInDebug("alarm ${reminder.alerts?.get(0)?.id} onReceive")

        // TODO: 15/12/21 maybe go here from notification click!

        FlowLog.alertBroadcastIsCalled()

        context?.let {
            intent?.getLongExtra(Reminder.KEY_REMINDER_ID, -1)?.let { id ->
                val reminder = runBlocking { AppDatabase.getInstance()?.reminderDao()!!.findById(id) }
                FlowLog.alertBroadcastGotReminder(id, reminder)

                if (reminder != null) {
                    if (Calendar.getInstance().timeInMillis - reminder.nextAlarmWithSnooze() < 3600000) { // 3600000 millisecond is one hour
                        val alertIntent = Utils.getAlertIntent(id)
                        context.startActivity(alertIntent)
                    } else {
                        FlowLog.alertBroadcastReminderOutOfRange(reminder)

                    }
                }
            }

        }

    }
}