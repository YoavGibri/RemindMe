package com.hirshler.remindme.ui.reminderlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirshler.remindme.managers.AlertsManager
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.ReminderRepo
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.set

class RemindersListViewModel : ViewModel() {

    val reminders = MutableLiveData<MutableList<Reminder>>(mutableListOf())


    private val remindersLiveData = ReminderRepo().getAllLiveData()
    private val observer = Observer<MutableList<Reminder>> { list ->
        list.apply {

            sortWith(compareByDescending { it.nextAlarmWithSnooze() })

            if (any { reminder -> !reminder.repeat && reminder.nextAlarmWithSnooze() >= Calendar.getInstance().timeInMillis })
                add(Reminder(isListTitle = true, text = "Active", manualAlarm = Calendar.getInstance().apply { add(Calendar.MINUTE, 1) }.timeInMillis))

            if (any { reminder -> !reminder.repeat && reminder.nextAlarmWithSnooze() < Calendar.getInstance().timeInMillis }) {
                add(Reminder(isListTitle = true, text = "Dismissed", manualAlarm = -1))
            }

            if (any { reminder -> reminder.repeat }) {
                add(Reminder(isListTitle = true, text = "Repeat").apply { weekDays[1] = true })
            }


            sortWith(compareBy({ it.nextAlarmWithSnooze() < Calendar.getInstance().timeInMillis }, { it.repeat }, { !it.isListTitle }))
        }
        reminders.value = list
    }

    init {
        remindersLiveData.observeForever(observer)
    }


    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            ReminderRepo().delete(reminder)
            AlertsManager.cancelAlert(reminder)
        }
    }


    override fun onCleared() {
        remindersLiveData.removeObserver(observer)
    }
}