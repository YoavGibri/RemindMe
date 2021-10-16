package com.hirshler.remindme.room

import android.util.Log
import com.hirshler.remindme.model.Reminder



class ReminderRepo {
    private val TAG = "ReminderRepo"

    var db: ReminderDao = AppDatabase.getInstance()?.reminderDao()!!


    //Fetch Reminder by id
    fun findById(id: Int): Reminder {
        return db.findById(id)
    }

    //Fetch All the Reminders
    fun getAll(): List<Reminder> {
        return db.getAll()
    }

    suspend fun getAllForList(): MutableList<Reminder> {
        var reminders = db.getAll()
        reminders.add(Reminder(id = 0, text="Repeat Reminders", repeat = true))
        reminders.add(Reminder(id = 0, text="Dismissed Reminders", isDismissed = true))
        reminders.sortBy { it.isDismissed }
        reminders.sortBy { it.repeat }
        reminders.sortBy { it.nextAlarmTime }

        return reminders
    }

    // Insert new reminder
    fun insert(reminder: Reminder) {
        db.insert(reminder)
        Log.d(TAG, "new reminder was added")
    }

    // update reminder
    fun update(reminder: Reminder) {
        db.update(reminder)
    }

    // Delete reminder
    fun delete(reminder: Reminder) {
        db.delete(reminder)
    }

    fun setAsDismissed(reminder: Reminder){
        reminder.isDismissed = true
        db.update(reminder)
    }


}