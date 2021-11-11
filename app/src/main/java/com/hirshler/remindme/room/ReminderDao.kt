package com.hirshler.remindme.room

import androidx.room.*
import com.hirshler.remindme.model.Reminder


@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reminder: Reminder) : Long

    @Query("select * from Reminder where id = :id")
    suspend fun findById(id: Long): Reminder

    @Query("select * from Reminder")
    suspend fun getAll(): MutableList<Reminder>

    @Update
    suspend fun update(reminder: Reminder) : Int

    @Delete
    suspend fun delete(reminder: Reminder)

//    @Transaction
//    suspend fun upsert(reminder: Reminder) {
//        val id = insert(reminder)
//        if (id == -1L) {
//            update(reminder)
//        }
//    }

}