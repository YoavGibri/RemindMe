package com.hirshler.remindme.room

import android.util.Log
import com.hirshler.remindme.model.Reminder


class ReminderRepo {
    private val TAG = "ReminderRepo"

    var db: ReminderDao = AppDatabase.getInstance()?.reminderDao()!!


    //Fetch Reminder by id
    suspend fun findById(id: Long): Reminder {
        return db.findById(id)
    }

    //Fetch All the Reminders
    suspend fun getAll(): List<Reminder> {
        return db.getAll()
    }

    suspend fun getAllForList(): MutableList<Reminder> {
        val reminders = db.getAll()

        if (reminders.isNotEmpty())
            reminders.add(Reminder(text = "Active", nextAlarmTime = 0))

        if (reminders.any { reminder -> reminder.isDismissed }) {
            reminders.add(Reminder(text = "Dismissed", isDismissed = true, nextAlarmTime = 0))
        }

        if (reminders.any { reminder -> reminder.repeat }) {
            reminders.add(Reminder(text = "Repeat", repeat = true, nextAlarmTime = 0))
        }


        reminders.sortWith(compareBy({ it.isDismissed }, { it.repeat }, { it.id }))

        return reminders
    }

    // Insert new reminder
    suspend fun insert(reminder: Reminder): Long {
        val id = db.insert(reminder)
        Log.d(TAG, "new reminder was added")
        return id
    }

    // update reminder
    suspend fun update(reminder: Reminder) {
        db.update(reminder)
    }

    // Delete reminder
    suspend fun delete(reminder: Reminder) {
        db.delete(reminder)
    }

    suspend fun setAsDismissed(reminder: Reminder) {
        reminder.isDismissed = true
        db.update(reminder)
    }


}