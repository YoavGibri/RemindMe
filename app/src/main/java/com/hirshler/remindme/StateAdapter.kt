package com.hirshler.remindme

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.ui.reminder.ReminderFragment
import com.hirshler.remindme.ui.reminderlist.ReminderListFragment
import com.hirshler.remindme.ui.settings.SettingsFragment

class StateAdapter(activity: FragmentActivity, val reminder: Reminder?) : FragmentStateAdapter(activity) {
    lateinit var settingsFragment: SettingsFragment
    lateinit var reminderFragment: ReminderFragment

    override fun getItemCount(): Int {
        return if (reminder != null) 1 else 3
    }

    override fun createFragment(position: Int): Fragment {

        return if (reminder != null) {

            ReminderFragment(reminder)

        } else when (position) {

            0 -> ReminderFragment().also { reminderFragment = it }
            1 -> ReminderListFragment()
            2 -> SettingsFragment().also { settingsFragment = it  }
            else -> SettingsFragment()
        }

    }


}