package com.hirshler.remindme

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
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
        firebaseAnalytics = Firebase.analytics
    }


    companion object {
        private var instance: Application? = null
        lateinit var firebaseAnalytics: FirebaseAnalytics

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