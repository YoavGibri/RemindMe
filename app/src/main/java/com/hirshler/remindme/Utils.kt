package com.hirshler.remindme

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.hirshler.remindme.model.Reminder
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

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

            val formatter = SimpleDateFormat("dd/MM/yy kk:mm:ss", Locale.getDefault())
            val cal = Calendar.getInstance().apply { timeInMillis = millis }.time
            return formatter.format(cal)
        }

        fun writeToFile(data: String) {
            try {
                val outputStreamWriter = OutputStreamWriter(App.applicationContext().openFileOutput("log.txt", Context.MODE_PRIVATE))
                outputStreamWriter.write(data)
                outputStreamWriter.close()
            } catch (e: IOException) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        fun readFromFile(): String {
            var ret = ""
            try {
                val inputStream: InputStream? = App.applicationContext().openFileInput("log.txt")
                if (inputStream != null) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    var receiveString: String? = ""
                    val stringBuilder = StringBuilder()
                    while (bufferedReader.readLine().also { receiveString = it } != null) {
                        stringBuilder.append("\n").append(receiveString)
                    }
                    inputStream.close()
                    ret = stringBuilder.toString()
                }
            } catch (e: FileNotFoundException) {
                Log.e("login activity", "File not found: " + e.toString())
            } catch (e: IOException) {
                Log.e("login activity", "Can not read file: $e")
            }
            return ret
        }
    }

}