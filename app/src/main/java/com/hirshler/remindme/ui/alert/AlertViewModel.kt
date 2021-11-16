package com.hirshler.remindme.ui.alert

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


    val currentReminder = MutableLiveData<Reminder>(null)

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
        currentReminder.value = runBlocking {
            ReminderRepo().let {
                it.update(currentReminder.value!!)
                it.findById(currentReminder.value!!.id!!)
            }

        }
    }


    fun setNextAlert() {
        currentReminder.value?.let { AlertsManager.setNextAlert(it) }
    }


//    fun setAlert() {
//        Log.d("alertviewmodel", "setAlert")
//
//        currentReminder.value?.apply {
//            if (hasNextAlert()) {
//                AlertsManager.setAlert(this)
//            } else
//                AlertsManager.cancelAlert(this)
//        }
//    }

    fun dismissReminder() {
        viewModelScope.launch {
            currentReminder.value?.let { ReminderRepo().setAsDismissed(it) }
        }
    }

    fun initCurrentReminderById(id: Long) {
        viewModelScope.launch {
            currentReminder.value = ReminderRepo().findById(id)
        }
    }


}
