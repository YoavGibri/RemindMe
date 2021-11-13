package com.hirshler.remindme.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.WindowManager
import android.widget.Toast
import com.hirshler.remindme.model.Reminder.Companion.KEY_REMINDER_ID

class AlertReceiver : BroadcastReceiver() {
    private val toast: Toast? = null

    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {

//        val reminderId = intent?.getLongExtra(KEY_REMINDER_ID, -1)

//        toast.showInDebug("alarm ${reminder.alerts?.get(0)?.id} onReceive")


        val alertIntent = Intent()
        alertIntent.setClassName(context?.packageName!!, context.packageName + ".activities.AlertActivity")

        alertIntent.putExtra(KEY_REMINDER_ID, intent?.getLongExtra(KEY_REMINDER_ID, -1))
        alertIntent.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON +
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON +
                    WindowManager.LayoutParams.FLAG_FULLSCREEN + FLAG_ACTIVITY_NEW_TASK
        )

        context.startActivity(alertIntent)

    }
}