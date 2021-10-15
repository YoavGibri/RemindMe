package com.hirshler.remindme.ui.alert

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hirshler.remindme.AlertsManager
import com.hirshler.remindme.TimeManager
import com.hirshler.remindme.model.Alert
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.AppDatabase
import com.hirshler.remindme.room.ReminderRepo
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

    fun setDays(days: Int) {
        currentCalendar.value = TimeManager.setDays(days, currentCalendar.value!!.get(Calendar.MINUTE))
    }

    fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        currentCalendar.value = TimeManager.setDate(year, monthOfYear, dayOfMonth, currentCalendar.value!!.get(
            Calendar.MINUTE))
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        currentCalendar.value = TimeManager.setTime(hourOfDay, minute, currentCalendar.value!!.get(
            Calendar.DAY_OF_YEAR));
    }


    fun createReminder(text: String) {
        currentReminder.value = Reminder().apply {
            this.text = text
            delayInMinutes = minutesDelay
            alerts = listOf(Alert(0, currentCalendar.value!!.timeInMillis))
        }
    }

    fun saveReminderToDb(context: Context) {
        AppDatabase.getInstance(context)
        val reminderRepo = ReminderRepo(context)
        reminderRepo.insert(currentReminder.value!!)
        //reminderRepo.getAll().forEach { Log.d("ReminderFragment", Gson().toJson(it)) }
    }

    fun setAlerts(context: Context) {
        currentReminder.value!!.alerts?.forEach {
            AlertsManager.setAlert(currentReminder.value!!, it)
        }
    }
}