package com.hirshler.remindme.ui.alert

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirshler.remindme.managers.AlertsManager
import com.hirshler.remindme.managers.TimeManager
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.ReminderRepo
import com.hirshler.remindme.timeOfDayInMinutes
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class AlertViewModel : ViewModel() {
    //    var alertWasDismissed: Boolean = false
    val TAG = "AlertViewModel"

    val currentReminder = MutableLiveData<Reminder>(null)

    val currentCalendar = MutableLiveData<Calendar>(Calendar.getInstance())

    //var minutesDelay = 0
     var origSnooze = 0
    var currentSnooze = 0


    fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        currentCalendar.value = TimeManager.setDate(
            year, monthOfYear, dayOfMonth, currentCalendar.value!!.get(Calendar.HOUR_OF_DAY), currentCalendar.value!!.get(Calendar.MINUTE)
        )
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        currentCalendar.value = TimeManager.setTime(hourOfDay, minute, currentCalendar.value!!.get(Calendar.DAY_OF_YEAR));
    }

    fun updateCurrentReminder() {
        currentReminder.value?.let {
            it.snooze = origSnooze + currentSnooze
            if (it.isRepeat) {
                it.alarmTimeOfDay = currentCalendar.value!!.apply { set(Calendar.SECOND, 0) }.timeOfDayInMinutes()
            } else {
                it.manualAlarm = currentCalendar.value!!.apply { set(Calendar.SECOND, 0) }.timeInMillis
            }
        }
    }

    fun saveReminderToDb() {
        currentReminder.value = runBlocking {
            Log.d(TAG, "saveReminderToDb: ${currentReminder.value!!}")
            ReminderRepo().update(currentReminder.value!!)
        }
    }


    fun setNextAlert() {
        currentReminder.value?.let {
            AlertsManager.setNextAlert(it)
        }
    }


    fun dismissReminder() {
        viewModelScope.launch {
            currentReminder.value?.let {
                it.snooze = 0
                it.snoozeCount = 0
                it.dismissed = true
                ReminderRepo().update(it)
            }
        }
    }

    fun initCurrentReminderById(id: Long) {
        viewModelScope.launch {
            ReminderRepo().findById(id)?.let {
                val reminderFromDb = it
                //test
               //reminderFromDb.snooze = 60

                origSnooze = reminderFromDb.snooze
                currentReminder.value = reminderFromDb
            }

        }
    }


    fun resetSnooze() {
        origSnooze = 0
        currentSnooze = 0
    }

    fun setCalendarByReminder() {
        currentCalendar.value = Calendar.getInstance().apply { timeInMillis = currentReminder.value!!.nextAlarm() }
        Log.d(TAG, "setCalendarByReminder: ")
    }


}
