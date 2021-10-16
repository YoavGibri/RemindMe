package com.hirshler.remindme.room

import androidx.room.*
import com.hirshler.remindme.model.Reminder

@Dao
interface ReminderDao {

    @Insert
    fun insert(reminder: Reminder)

    @Query("select * from Reminder where id = :id")
    fun findById(id: Int): Reminder

    @Query("select * from Reminder")
    fun getAll(): MutableList<Reminder>

    @Update
    fun update(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)

}