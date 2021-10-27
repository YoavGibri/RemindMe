package com.hirshler.remindme.ui.reminder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hirshler.remindme.AlertsManager
import com.hirshler.remindme.TimeManager
import com.hirshler.remindme.model.Alert
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.ReminderRepo
import java.util.*

class ReminderViewModel : ViewModel() {


    val currentReminder = MutableLiveData<Reminder>(Reminder())

    val currentCalendar = MutableLiveData<Calendar>(Calendar.getInstance())
    var minutesDelay: Int = 0

    fun setMinutes(minutes: Int) {
        minutesDelay = minutes
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


    fun createReminder(text: String) {
        currentReminder.value = Reminder().apply {
            this.text = text
            delayInMinutes = minutesDelay
            val time = currentCalendar.value!!.timeInMillis
            alerts = listOf(Alert((time % 100000), time))
        }
    }

    fun saveReminderToDb() {
        val reminderRepo = ReminderRepo()
        val id = reminderRepo.insert(currentReminder.value!!)
        currentReminder.value?.id = id
    }

    fun setAlerts() {
        val tempTime = Calendar.getInstance().apply { add(Calendar.SECOND, 5) }.timeInMillis
        currentReminder.value!!.alerts?.forEach {
            AlertsManager.setAlert(currentReminder.value!!, it, tempTime)
        }
    }



}