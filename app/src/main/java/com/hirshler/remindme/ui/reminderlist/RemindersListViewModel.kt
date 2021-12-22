package com.hirshler.remindme.ui.reminderlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.ReminderRepo
import kotlinx.coroutines.launch

class RemindersListViewModel : ViewModel() {

    val reminders = MutableLiveData<MutableList<Reminder>>(mutableListOf())


    private val remindersLiveData = ReminderRepo().getAllLD()
    private val observer = Observer<MutableList<Reminder>> { list ->
        list.apply {

            if (any { reminder -> !reminder.dismissed && !reminder.repeat })
                add(Reminder(text = "Active"))

            if (any { reminder -> reminder.dismissed }) {
                add(Reminder(text = "Dismissed", dismissed = true))
            }

            if (any { reminder -> reminder.repeat }) {
                add(Reminder(text = "Repeat", repeat = true))
            }

            sortWith(compareBy({ it.dismissed }, { it.repeat }, { it.id }))
        }
        reminders.value = list
    }

    init {
        remindersLiveData.observeForever(observer)
    }


    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            ReminderRepo().delete(reminder)
        }
    }


    override fun onCleared() {
        remindersLiveData.removeObserver(observer)
    }
}