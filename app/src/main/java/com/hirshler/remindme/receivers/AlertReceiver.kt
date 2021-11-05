package com.hirshler.remindme.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.gson.Gson
import com.hirshler.remindme.AlertsManager
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.showInDebug
import java.util.*

class AlertReceiver : BroadcastReceiver() {
    private val toast: Toast? = null

    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlertReceiver", Gson().toJson(intent))

        val reminder = Gson().fromJson(intent?.getStringExtra("reminder"), Reminder::class.java)


        toast.showInDebug("alarm ${reminder.alerts?.get(0)?.id} onReceive")

        // set snooze reminder
        reminder.alerts?.get(0)?.time = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }.timeInMillis


        AlertsManager.setAlert(reminder, reminder.alerts?.get(0)!!)


        val alertIntent = Intent()
        alertIntent.setClassName(
            context?.packageName!!,
            context.packageName + ".activities.AlertActivity"
        )
        alertIntent.putExtra("reminder", intent?.getStringExtra("reminder"))
        alertIntent.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON +
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON +
                    WindowManager.LayoutParams.FLAG_FULLSCREEN + FLAG_ACTIVITY_NEW_TASK
        )

        context.startActivity(alertIntent)

//        context?.startActivity(
//            Intent(context, MainActivity::class.java).apply {
//                addFlags(FLAG_ACTIVITY_NEW_TASK)
//                putExtra("reminder", intent?.getStringExtra("reminder"))
//            }
//        )
    }
}