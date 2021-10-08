package com.hirshler.remindme.ui.reminder

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.AppDatabase
import com.hirshler.remindme.room.ReminderRepo
import java.util.*

class ReminderViewModel : ViewModel() {


    val currentReminder = MutableLiveData<Reminder>(Reminder())

    val currentCalendar = MutableLiveData<Calendar>(Calendar.getInstance())
    var minutesDelay: Int = 0

    fun setMinutes(minutes: Int) {
        minutesDelay = minutes
        val cal = Calendar.getInstance()
        //cal.set(Calendar.DAY_OF_YEAR, currentCalendar.value!!.get(Calendar.DAY_OF_YEAR))
        cal.add(Calendar.MINUTE, minutes)
        currentCalendar.value = cal;
    }

    fun setDays(days: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MINUTE, currentCalendar.value!!.get(Calendar.MINUTE))
        cal.add(Calendar.DAY_OF_YEAR, days)
        currentCalendar.value = cal;
    }

    fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MINUTE, currentCalendar.value!!.get(Calendar.MINUTE))
        cal.set(year, monthOfYear, dayOfMonth)
        currentCalendar.value = cal;
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_YEAR, currentCalendar.value!!.get(Calendar.DAY_OF_YEAR))
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)
        currentCalendar.value = cal;
    }

    fun saveReminderToDb(context: Context) {
        AppDatabase.getInstance(context)
        val reminderRepo = ReminderRepo(context)
        reminderRepo.insert(currentReminder.value!!)
        reminderRepo.getAll().forEach { Log.d("ReminderFragment", Gson().toJson(it)) }
    }

}