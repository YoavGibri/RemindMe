package com.hirshler.remindme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.hirshler.remindme.activities.AlertActivity
import com.hirshler.remindme.model.Reminder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Utils {

    companion object {
        fun hideKeyboard(view: View) {
            val inputMethodManager = ContextCompat.getSystemService(App.applicationContext(), InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        @SuppressLint("WrongConstant")
        fun getAlertIntent(reminderId: Long): Intent {
            return Intent(App.applicationContext(), AlertActivity::class.java).apply {
                //setClassName(App.applicationContext().packageName, App.applicationContext().packageName + ".activities.AlertActivity")
                putExtra(Reminder.KEY_REMINDER_ID, reminderId)
                addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON +
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON +
                            WindowManager.LayoutParams.FLAG_FULLSCREEN + Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }
        }

        fun fullDateByMilliseconds(millis: Long): String {
            if (millis == 0L) return "not available"

            val formatter = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
            val cal = Calendar.getInstance().apply { timeInMillis = millis }.time
            return formatter.format(cal)
        }


        val logFilePath = File(App.applicationContext().getExternalFilesDir("RemindMe"), "RemindMe21_log.txt")
        fun writeToFile(data: String, append: Boolean) {

            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(logFilePath, append)
                fileOutputStream.write(data.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }


        fun readFromFile(): String {
            var fileInputStream: FileInputStream? = null
            try {
                fileInputStream = FileInputStream(logFilePath)
                var i: Int
                val buffer = StringBuffer()
                while (fileInputStream.read().also { i = it } != -1) {
                    buffer.append(i.toChar())
                }
                return buffer.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return "no log"
        }

        fun sendEmail(context: Activity) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            val uri = Uri.fromFile(logFilePath)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(Intent.createChooser(intent, "Send eMail.."))
        }
    }

}