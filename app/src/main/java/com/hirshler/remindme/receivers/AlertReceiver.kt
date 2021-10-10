package com.hirshler.remindme.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.view.WindowManager
import com.google.gson.Gson

class AlertReceiver : BroadcastReceiver() {
    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {
        // TODO: 09/10/21 add receiver to manifest
        Log.d("AlertReceiver", Gson().toJson(intent))
        Log.d("AlertReceiver", context.toString())

        val alertIntent = Intent()
        alertIntent.setClassName(context?.packageName!!, context.packageName + ".activities.AlertActivity")
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