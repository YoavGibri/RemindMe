package com.hirshler.remindme

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import com.hirshler.remindme.room.AppDatabase
import com.hirshler.remindme.room.ReminderRepo

class App : Application() {
    val database by lazy { AppDatabase.getInstance() }
    val repository by lazy { ReminderRepo() }

    init {
        instance = this
    }


    override fun onCreate() {
        super.onCreate()
        NotificationsManager.createNotificationChannel()

        initVolumes()
    }

    private fun initVolumes() {

    }

    companion object {
        private var instance: Application? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun showError(context: Context, message: String) {
            AlertDialog.Builder(context)
                .setTitle("error")
                .setMessage(message)
                .setPositiveButton("ok", null)
                .show()
        }

    }

}