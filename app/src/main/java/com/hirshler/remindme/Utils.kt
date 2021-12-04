package com.hirshler.remindme

import android.content.Intent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.hirshler.remindme.model.Reminder

class Utils {

    companion object {
        fun hideKeyboard(view: View) {
            val inputMethodManager = ContextCompat.getSystemService(App.applicationContext(), InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun getAlertIntent(reminderId: Long): Intent {
            return Intent().apply {
                setClassName(App.applicationContext().packageName, App.applicationContext().packageName + ".activities.AlertActivity")
                putExtra(Reminder.KEY_REMINDER_ID, reminderId)
                addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON +
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON +
                            WindowManager.LayoutParams.FLAG_FULLSCREEN + Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }
        }
    }

}