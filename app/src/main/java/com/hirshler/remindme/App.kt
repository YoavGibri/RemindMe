package com.hirshler.remindme

import android.app.Application
import android.content.Context
import com.hirshler.remindme.room.AppDatabase
import com.hirshler.remindme.room.ReminderRepo

class App : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { ReminderRepo(this) }

    init {
        instance = this
    }

    companion object {
        private var instance: Application? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

}