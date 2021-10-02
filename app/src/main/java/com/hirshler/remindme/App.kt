package com.hirshler.remindme

import android.app.Application
import com.hirshler.remindme.room.AppDatabase
import com.hirshler.remindme.room.ReminderRepo

class App : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { ReminderRepo(this) }
}