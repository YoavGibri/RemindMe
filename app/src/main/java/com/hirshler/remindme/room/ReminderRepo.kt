package com.hirshler.remindme.room

import androidx.lifecycle.LiveData
import com.hirshler.remindme.FlowLog
import com.hirshler.remindme.model.Reminder


class ReminderRepo {
    private val TAG = "ReminderRepo"

    var db: ReminderDao = AppDatabase.getInstance()?.reminderDao()!!


    //Fetch Reminder by id
    suspend fun findById(id: Long): Reminder? {
        return db.findById(id)
    }

    //Fetch All the Reminders
    suspend fun getAll(): List<Reminder> {
        return db.getAll()
    }

    //Fetch All the Reminders
    fun getAllLiveData(): LiveData<MutableList<Reminder>> {
        return db.getAllLD()
    }


    suspend fun upsert(reminder: Reminder): Long {
        var id = db.insert(reminder)

        if (id != -1L) {
            FlowLog.newReminder(reminder.apply { reminder.id = id })

        } else {
            db.update(reminder)
            FlowLog.reminderUpdate(reminder)
            id = reminder.id!!
        }

        return id
    }

    // update reminder
    suspend fun update(reminder: Reminder): Reminder {
        db.update(reminder)
        FlowLog.reminderUpdate(reminder)
        return db.findById(reminder.id!!)!!
    }

    // Delete reminder
    suspend fun delete(reminder: Reminder) {
        db.delete(reminder)
        FlowLog.reminderDelete(reminder)
    }


}