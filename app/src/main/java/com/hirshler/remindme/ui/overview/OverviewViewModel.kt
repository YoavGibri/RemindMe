package com.hirshler.remindme.ui.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.ReminderRepo
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {

    val reminders = MutableLiveData<MutableList<Reminder>>(mutableListOf())

    fun getReminders() {
        viewModelScope.launch {
            reminders.value = ReminderRepo().getAllForList()
        }
    }
}