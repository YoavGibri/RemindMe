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

class RemindersListViewModel : ViewModel() {

    val reminders = MutableLiveData<MutableList<Reminder>>(mutableListOf())


    private val remindersLiveData = ReminderRepo().getAllLiveData()
    private val observer = Observer<MutableList<Reminder>> { list ->


        val now = Calendar.getInstance().timeInMillis
        val newList = mutableListOf<Reminder>()

        list.run {
            // Active
            filter { !it.isRepeat && it.nextAlarmWithSnooze() > now }.sortedBy { it.nextAlarmWithSnooze() }
                .let {
                    if (it.isNotEmpty()) {
                        newList.add(Reminder(isListTitle = true, text = "Active"))
                        newList.addAll(it)
                    }
                }

            //Repeat
            filter { it.isRepeat }.sortedBy { it.nextAlarmWithSnooze() }
                .let {
                    if (it.isNotEmpty()) {
                        newList.add(Reminder(isListTitle = true, text = "Repeat"))
                        newList.addAll(it)
                    }
                }

            //Dismissed
            filter { !it.isRepeat && it.nextAlarmWithSnooze() < now }.sortedByDescending { it.nextAlarmWithSnooze() }
                .let {
                    if (it.isNotEmpty()) {
                        newList.add(Reminder(isListTitle = true, text = "Dismissed"))
                        newList.addAll(it)
                    }
                }
        }


        reminders.value = newList
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