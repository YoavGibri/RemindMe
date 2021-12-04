package com.hirshler.remindme.ui.alert

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirshler.remindme.AlertsManager
import com.hirshler.remindme.TimeManager
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.ReminderRepo
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class AlertViewModel : ViewModel() {
    val TAG = "AlertViewModel"

    val currentReminder = MutableLiveData<Reminder>(null)

    val currentCalendar = MutableLiveData<Calendar>(null)

    //var minutesDelay = 0
    private var origSnooze = 0
    private var currentSnooze = 0

//    fun setMinutes(minutes: Int) {
//        minutesDelay = minutes
//        currentCalendar.value = TimeManager.setMinutes(minutes)
//    }


    fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        currentCalendar.value = TimeManager.setDate(
            year, monthOfYear, dayOfMonth, currentCalendar.value!!.get(Calendar.HOUR_OF_DAY), currentCalendar.value!!.get(Calendar.MINUTE)
        )
    }


    fun updateCurrentReminder() {
        currentReminder.value?.apply {
            snooze = origSnooze + currentSnooze
            manualAlarm = currentCalendar.value!!.timeInMillis
        }
    }

    fun saveReminderToDb() {
        currentReminder.value = runBlocking {
            Log.d(TAG, "saveReminderToDb: ${currentReminder.value!!}")
            ReminderRepo().update(currentReminder.value!!)
//            ReminderRepo().let {
//                it.update(currentReminder.value!!)
//                it.findById(currentReminder.value!!.id!!)
//            }

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
                it.manualAlarm = 0
                it.snooze = 0
                it.dismissed = true
                ReminderRepo().update(it)
            }
        }
    }

    fun initCurrentReminderById(id: Long) {
        viewModelScope.launch {
            ReminderRepo().findById(id).let {
                origSnooze = it.snooze
                currentReminder.value = it
            }

        }
    }

    fun updateSnooze(minutes: Int) {
        Log.d(TAG, "updateSnooze: ${currentReminder.value!!}")
        currentSnooze += minutes
    }

    fun setCalendarByReminder() {
        currentCalendar.value = Calendar.getInstance().apply { timeInMillis = currentReminder.value!!.nextAlarmTime }
    }


}
