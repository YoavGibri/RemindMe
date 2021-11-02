package com.hirshler.remindme.ui.alert

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirshler.remindme.AlertsManager
import com.hirshler.remindme.TimeManager
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.ReminderRepo
import kotlinx.coroutines.launch
import java.util.*

class AlertViewModel : ViewModel() {


    val currentReminder = MutableLiveData<Reminder>(Reminder())
    val origReminder = MutableLiveData<Reminder>(Reminder())

    val currentCalendar = MutableLiveData<Calendar>(Calendar.getInstance())
    var minutesDelay: Int = 0

    fun setMinutes(minutes: Int) {
        minutesDelay = minutes
        currentCalendar.value = TimeManager.setMinutes(minutes)
    }


    fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        currentCalendar.value = TimeManager.setDate(
            year, monthOfYear, dayOfMonth, currentCalendar.value!!.get(
                Calendar.MINUTE
            )
        )
    }


    fun updateReminder() {
        currentReminder.value?.apply {
            delayInMinutes = minutesDelay
            alerts?.get(0)?.time = currentCalendar.value!!.timeInMillis
        }
    }

    fun saveReminderToDb() {
        viewModelScope.launch {
            ReminderRepo().update(currentReminder.value!!)
            //reminderRepo.getAll().forEach { Log.d("ReminderFragment", Gson().toJson(it)) }
        }
    }

    fun setAlerts() {
        currentReminder.value!!.alerts?.forEach {
            AlertsManager.setAlert(currentReminder.value!!, it)
        }
    }

    fun dismissReminder() {
        viewModelScope.launch {
            origReminder.value?.let { ReminderRepo().setAsDismissed(it) }
        }
    }


}
