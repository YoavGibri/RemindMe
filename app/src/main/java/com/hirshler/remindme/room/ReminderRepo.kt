package com.hirshler.remindme.room

import android.content.Context
import android.nfc.Tag
import android.util.Log
import com.hirshler.remindme.model.Reminder



class ReminderRepo(context: Context) {
    private val TAG = "ReminderRepo"

    var db: ReminderDao = AppDatabase.getInstance(context)?.reminderDao()!!


    //Fetch Reminder by id
    fun findById(id: Int): Reminder {
        return db.findById(id)
    }

    //Fetch All the Reminders
    fun getAll(): List<Reminder> {
        return db.getAll()
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


}