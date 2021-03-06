package com.hirshler.remindme.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hirshler.remindme.App
import com.hirshler.remindme.model.Reminder


@Database(entities = [Reminder::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room
                        .databaseBuilder(App.applicationContext(), AppDatabase::class.java, "remindme.db")
//                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }


    }
}
