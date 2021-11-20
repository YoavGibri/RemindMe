package com.hirshler.remindme.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hirshler.remindme.AlertsManager
import com.hirshler.remindme.room.ReminderRepo
import kotlinx.coroutines.runBlocking
import java.util.*

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.i("onReceive", "start")

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("onReceive", "inside action")

            val now = Calendar.getInstance().timeInMillis
            runBlocking {
                ReminderRepo()
                    .getAll()
                    .filter { reminder -> reminder.dismissed.not() }
                    .forEach {
                        AlertsManager.setNextAlert(it)
                    }
            }
        }

    }
}