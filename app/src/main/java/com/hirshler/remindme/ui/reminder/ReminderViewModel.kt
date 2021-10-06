package com.hirshler.remindme.ui.reminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hirshler.remindme.model.Reminder
import java.util.*

class ReminderViewModel : ViewModel() {


    val currentReminder = MutableLiveData<Reminder>(Reminder())

    var currentCalendar = MutableLiveData<Calendar>(Calendar.getInstance())

    fun setMinutes(minutes: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_YEAR, currentCalendar.value!!.get(Calendar.DAY_OF_YEAR))
        cal.add(Calendar.MINUTE, minutes)
        currentCalendar.value = cal;
    }

    fun setDays(days: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MINUTE, currentCalendar.value!!.get(Calendar.MINUTE))
        cal.add(Calendar.DAY_OF_YEAR, days)
        currentCalendar.value = cal;
    }

}