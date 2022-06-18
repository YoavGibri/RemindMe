package com.hirshler.remindme.ui.reminder

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hirshler.remindme.AppSettings
import com.hirshler.remindme.managers.AlertsManager
import com.hirshler.remindme.managers.TimeManager
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.ReminderRepo
import com.hirshler.remindme.timeOfDayInMinutes
import kotlinx.coroutines.runBlocking
import java.util.*

class ReminderViewModel : ViewModel() {


    val currentReminder = MutableLiveData<Reminder>(Reminder())

    val currentCalendar = MutableLiveData<Calendar>(Calendar.getInstance().apply { add(Calendar.MINUTE, 5) })

    fun setMinutes(minutes: Int) {
        currentCalendar.value = TimeManager.setMinutes(minutes)
    }

    fun setDays(days: Int) {
        currentCalendar.value = TimeManager.setDays(days, currentCalendar.value!!.get(Calendar.HOUR_OF_DAY), currentCalendar.value!!.get(Calendar.MINUTE))
    }

    fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        currentCalendar.value =
            TimeManager.setDate(year, monthOfYear, dayOfMonth, currentCalendar.value!!.get(Calendar.HOUR_OF_DAY), currentCalendar.value!!.get(Calendar.MINUTE))
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        currentCalendar.value = TimeManager.setTime(hourOfDay, minute, currentCalendar.value!!.get(Calendar.DAY_OF_YEAR));
    }


    fun createReminder() {
        currentReminder.value?.apply {
            if (isRepeat) {
                alarmTimeOfDay = currentCalendar.value!!.apply { set(Calendar.SECOND, 0) }.timeOfDayInMinutes()
            } else {
                manualAlarm = (
                        if (AppSettings.getIsDebugMode()) {
                            Calendar.getInstance().apply { add(Calendar.SECOND, 5) }
                        } else
                            currentCalendar.value!!.apply { set(Calendar.SECOND, 0) }
                        ).timeInMillis
            }
            snooze = 0
            snoozeCount = 0
        }
    }

    fun saveReminderToDb() {
        val reminderRepo = ReminderRepo()
        Log.d("viewmodel upsert", currentReminder.value!!.toString())

        val reminderId = runBlocking { reminderRepo.upsert(currentReminder.value!!) }

        currentReminder.value = currentReminder.value?.apply { id = reminderId }

        Log.d("viewmodel after upsert", currentReminder.value!!.toString())
    }

    fun setAlert() {
        Log.d("viewmodel", "setAlerts")
        AlertsManager.setNextAlert(currentReminder.value!!)
    }


}