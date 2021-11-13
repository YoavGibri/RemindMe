package com.hirshler.remindme.ui.reminder

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hirshler.remindme.AlertsManager
import com.hirshler.remindme.TimeManager
import com.hirshler.remindme.model.Alert
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.ReminderRepo
import kotlinx.coroutines.runBlocking
import java.util.*

class ReminderViewModel : ViewModel() {


    val currentReminder = MutableLiveData<Reminder>(Reminder())

    val currentCalendar = MutableLiveData<Calendar>(Calendar.getInstance())
    //var minutesDelay: Int = 0

    fun setMinutes(minutes: Int) {
        currentReminder.value?.delayInMinutes = minutes
        currentCalendar.value = TimeManager.setMinutes(minutes)
    }

    fun setDays(days: Int) {
        currentCalendar.value = TimeManager.setDays(days, currentCalendar.value!!.get(Calendar.MINUTE))
    }

    fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        currentCalendar.value = TimeManager.setDate(year, monthOfYear, dayOfMonth, currentCalendar.value!!.get(Calendar.MINUTE))
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        currentCalendar.value = TimeManager.setTime(hourOfDay, minute, currentCalendar.value!!.get(Calendar.DAY_OF_YEAR));
    }


    fun createReminder() {
        currentReminder.value?.apply {
            val time = currentCalendar.value!!.timeInMillis
            alerts = listOf(Alert((time % 100000), time))
        }
    }

    fun saveReminderToDb() {
        val reminderRepo = ReminderRepo()
        Log.d("viewmodel upsert", Gson().toJson(currentReminder.value!!))

        val reminderId = runBlocking { reminderRepo.upsert(currentReminder.value!!) }

        currentReminder.value = currentReminder.value?.apply { id = reminderId }

        Log.d("viewmodel after upsert", Gson().toJson(currentReminder.value!!))
    }

    fun setAlert() {
        Log.d("viewmodel", "setAlerts")
        val tempTime = Calendar.getInstance().apply { add(Calendar.SECOND, 5) }.timeInMillis

        AlertsManager.setNextAlert(currentReminder.value!!, tempTime)
    }



}